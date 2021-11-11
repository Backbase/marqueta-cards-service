package com.backbase.productled.service;

import static java.util.Objects.requireNonNull;

import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatus;
import com.backbase.presentation.card.rest.spec.v2.cards.ResetPinPost;
import com.backbase.productled.mapper.CardsMappers;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Service interacts with MarqetaService providing necessary validations
 */
@Service
@AllArgsConstructor
public class CardsService {

    private static final String TERMINATED = "TERMINATED";

    private final UserService userService;

    private final MarqetaService marqetaService;

    private final CardsMappers cardMapper;

    public List<CardItem> getCards(String userId, List<String> ids, List<String> status, List<String> types) {
        List<CardResponse> allCards = requireNonNull(marqetaService.getUserCards(
                userService.getMarqetaUserToken(userId))
            .getData());

        List<CardResponse> filteredCards = getNonTerminatedCards(allCards);
        Optional<CardResponse> recentlyTerminatedCard = getRecentlyTerminatedCard(allCards);
        recentlyTerminatedCard.ifPresent(filteredCards::add);

        return filteredCards.stream()
            .map(getCardResponseCardItemFunction())
            .filter(cardItem -> (ids == null || ids.contains(cardItem.getId())))
            .filter(cardItem -> (status == null || status.stream().anyMatch(cardItem.getStatus()::equalsIgnoreCase)))
            .filter(cardItem -> (types == null || types.contains(cardItem.getType())))
            .collect(Collectors.toList());
    }

    public CardItem getCard(String userId, String id) {
        var cardItems = getCards(userId, List.of(id), null, null);
        return Optional.ofNullable(cardItems).get().get(0);
    }

    public CardItem postLockStatus(String userId, String id, String lockStatus) {
        marqetaService.postCardTransitions(cardMapper.mapCardTransitionRequest(id,
            Objects.equals(lockStatus, LockStatus.UNLOCKED.getValue()) ? StateEnum.ACTIVE : StateEnum.SUSPENDED));
        return getCard(userId, id);
    }

    public CardItem activateCard(String id) {
        marqetaService.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.ACTIVE));
        return mapCardItem(marqetaService.updateCard(id, cardMapper.mapUpdateCardRequestForActivation(id)));
    }

    public CardItem resetPin(String userId, String id, ResetPinPost resetPinPost) {
        marqetaService.updatePin(getPin(id, resetPinPost));
        return getCard(userId, id);
    }

    public CardItem requestPin(String userId, String id) {
        return getCard(userId, id);
    }

    public CardItem requestReplacement(String userId, String id) {
        // change state of old card from Active to Terminated
        marqetaService.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.TERMINATED));
        // create new Card
        marqetaService.createCard(cardMapper.mapCreateCardRequest(marqetaService.getCardDetails(id)));
        return getCard(userId, id);
    }

    public CardItem changeLimits(String userId, String id, Map<String, BigDecimal> changeLimitsPostItem) {
        changeLimitsPostItem.entrySet().forEach(item -> {
            var velocityControlResponse = marqetaService.getCardLimitById(item.getKey());
            marqetaService.updateCardLimits(item.getKey(),
                cardMapper.mapVelocityControlUpdateRequest(velocityControlResponse, item.getValue()));
        });

        return getCard(userId, id);
    }

    private Function<CardResponse, CardItem> getCardResponseCardItemFunction() {
        return cardResponse -> cardMapper
            .mapCard(cardResponse, marqetaService.getCardLimits(cardResponse.getCardProductToken()));
    }

    private CardItem mapCardItem(CardResponse cardResponse) {
        return cardMapper.mapCard(cardResponse, marqetaService.getCardLimits(cardResponse.getCardProductToken()));
    }

    private PinRequest getPin(String id, ResetPinPost resetPinPost) {
        return new PinRequest()
            .controlToken(
                marqetaService.getPinControlToken(new ControlTokenRequest().cardToken(id)).getControlToken())
            .pin(resetPinPost.getPin());
    }

    private Optional<CardResponse> getRecentlyTerminatedCard(List<CardResponse> allCards) {
        return allCards.stream()
            .filter(cardResponse -> TERMINATED.equals(cardResponse.getState().toString()))
            .max(Comparator.comparing(CardResponse::getLastModifiedTime));
    }

    private List<CardResponse> getNonTerminatedCards(List<CardResponse> allCards) {
        return allCards.stream()
            .filter(cardResponse -> !TERMINATED.equals(cardResponse.getState().toString()))
            .collect(Collectors.toList());
    }

}
