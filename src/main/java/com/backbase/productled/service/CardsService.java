package com.backbase.productled.service;

import static java.util.Objects.requireNonNull;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.marqeta.clients.model.VelocityControlUpdateRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.ActivatePost;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.ChangeLimitsPostItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatus;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.ResetPinPost;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.MarqetaRepository;
import com.backbase.productled.repository.UserRepository;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CardsService {

    private final UserRepository userRepository;

    private final MarqetaRepository marqetaRepository;

    private final CardsMappers cardMapper;

    public List<CardItem> getCards(List<String> ids, List<String> status, List<String> types) {
        return requireNonNull(marqetaRepository.getUserCards(
            userRepository.getMarqetaUserToken())
            .getData()).stream()
            .map(getCardResponseCardItemFunction())
            .filter(getCardItemPredicate(ids, status, types))
            .collect(Collectors.toList());
    }

    public CardItem getCard(String id) {
        return mapCardItem(marqetaRepository.getCardDetails(id));
    }

    public CardItem postLockStatus(String id, LockStatusPost lockStatusPost) {
        marqetaRepository.postCardTransitions(cardMapper.mapCardTransitionRequest(id,
            lockStatusPost.getLockStatus() == LockStatus.UNLOCKED ? StateEnum.ACTIVE : StateEnum.SUSPENDED));
        return getCard(id);
    }

    public CardItem activateCard(String id, ActivatePost activatePost) {
        validateCvv(id, activatePost.getToken());
        marqetaRepository.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.ACTIVE));
        return mapCardItem(marqetaRepository.updateCard(id, cardMapper.mapUpdateCardRequestForActivation(id)));
    }

    public CardItem resetPin(String id, ResetPinPost resetPinPost) {
        validateCvv(id, resetPinPost.getToken());
        marqetaRepository.updatePin(getPin(id, resetPinPost));
        return getCard(id);
    }

    private void validateCvv(String cardToken, String cvv) {
        if (!cvv.equals(marqetaRepository.getCardCvv(cardToken).getCvvNumber())) {
            throw new BadRequestException()
                .withErrors(Collections.singletonList(new Error().withMessage("cvv is incorrect")));
        }
    }

    public CardItem requestReplacement(String id) {
        // change state of old card from Active to Terminated
        marqetaRepository.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.TERMINATED));
        // create new Card
        return mapCardItem(
            marqetaRepository.createCard(cardMapper.mapCreateCardRequest(marqetaRepository.getCardDetails(id))));
    }

    public CardItem changeLimits(String id, List<ChangeLimitsPostItem> changeLimitsPostItem) {
        changeLimitsPostItem.forEach(item -> marqetaRepository
            .updateCardLimits(item.getId(),
                new VelocityControlUpdateRequest().amountLimit(item.getAmount()).includeTransfers(true)));
        return getCard(id);
    }

    private Function<CardResponse, CardItem> getCardResponseCardItemFunction() {
        return cardResponse -> cardMapper
            .mapCard(cardResponse, marqetaRepository.getCardLimits(cardResponse.getCardProductToken()));
    }

    private CardItem mapCardItem(CardResponse cardResponse) {
        return cardMapper.mapCard(cardResponse, marqetaRepository.getCardLimits(cardResponse.getCardProductToken()));
    }

    private Predicate<CardItem> getCardItemPredicate(List<String> ids, List<String> status, List<String> types) {
        return cardItem ->
            (ids == null || ids.contains(cardItem.getId())) &&
                (status == null || status.contains(cardItem.getStatus()) &&
                    (types == null || types.contains(cardItem.getType())));
    }

    private PinRequest getPin(String id, ResetPinPost resetPinPost) {
        return new PinRequest()
            .controlToken(
                marqetaRepository.getPinControlToken(new ControlTokenRequest().cardToken(id)).getControlToken())
            .pin(resetPinPost.getPin());
    }
}
