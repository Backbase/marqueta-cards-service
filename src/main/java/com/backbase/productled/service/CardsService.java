package com.backbase.productled.service;

import static java.util.Objects.requireNonNull;

import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.ChangeLimitsPostItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatus;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.ResetPinPost;
import com.backbase.productled.mapper.CardsMappers;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CardsService {

    private static final String TERMINATED = "TERMINATED";

    private final UserService userService;

    private final MarqetaService marqetaService;

    private final CardsMappers cardMapper;

    public List<CardItem> getCards(List<String> ids, List<String> status, List<String> types) {
        List<CardResponse> allCards = requireNonNull(marqetaService.getUserCards(
                userService.getMarqetaUserToken())
            .getData());

        List<CardResponse> filteredCards = getNonTerminatedCards(allCards);
        Optional<CardResponse> recentlyTerminatedCard = getRecentlyTerminatedCard(allCards);
        recentlyTerminatedCard.ifPresent(filteredCards::add);

        return filteredCards.stream()
            .map(getCardResponseCardItemFunction())
            .filter(cardItem -> (ids == null || ids.contains(cardItem.getId())))
            .filter(cardItem -> (status == null || status.contains(cardItem.getStatus())))
            .filter(cardItem -> (types == null || types.contains(cardItem.getType())))
            .collect(Collectors.toList());
    }

    public CardItem getCard(String id) {
        return mapCardItem(marqetaService.getCardDetails(id));
    }

    public CardItem postLockStatus(String id, LockStatusPost lockStatusPost) {
        marqetaService.postCardTransitions(cardMapper.mapCardTransitionRequest(id,
            lockStatusPost.getLockStatus() == LockStatus.UNLOCKED ? StateEnum.ACTIVE : StateEnum.SUSPENDED));
        return getCard(id);
    }

    public CardItem activateCard(String id) {
        marqetaService.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.ACTIVE));
        return mapCardItem(marqetaService.updateCard(id, cardMapper.mapUpdateCardRequestForActivation(id)));
    }

    public CardItem resetPin(String id, ResetPinPost resetPinPost) {
        marqetaService.updatePin(getPin(id, resetPinPost));
        return getCard(id);
    }

    public CardItem requestPin(String id) {
        return getCard(id);
    }

    public CardItem requestReplacement(String id) {
        // change state of old card from Active to Terminated
        marqetaService.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.TERMINATED));
        // create new Card
        return mapCardItem(
            marqetaService.createCard(cardMapper.mapCreateCardRequest(marqetaService.getCardDetails(id))));
    }

    public CardItem changeLimits(String id, List<ChangeLimitsPostItem> changeLimitsPostItem) {
        changeLimitsPostItem.forEach(item -> {
            var velocityControlResponse = marqetaService.getCardLimitById(item.getId());
            marqetaService.updateCardLimits(item.getId(),
                cardMapper.mapVelocityControlUpdateRequest(velocityControlResponse, item.getAmount()));
        });

        return getCard(id);
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
