package com.backbase.productled.service;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.mambu.clients.model.Card;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.marqeta.clients.model.VelocityControlUpdateRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.ActivatePost;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.ChangeLimitsPostItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestReplacementPost;
import com.backbase.presentation.card.rest.spec.v2.cards.ResetPinPost;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.ArrangementRepository;
import com.backbase.productled.repository.MambuRepository;
import com.backbase.productled.repository.MarqetaRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CardsService {

    private final ArrangementRepository arrangementRepository;

    private final MarqetaRepository marqetaRepository;

    private final MambuRepository mambuRepository;

    private final CardsMappers cardMapper;

    public List<CardItem> getCards(List<String> ids, List<String> status, List<String> types) {

        return arrangementRepository.getExternalArrangementIds().stream()
            .map(mambuRepository::getCards)
            .collect(Collectors.toList())
            .stream().flatMap(List::stream)
            .collect(Collectors.toList()).stream()
            .map(Card::getReferenceToken)
            .map(marqetaRepository::getCardDetails)
            .map(cardResponse -> cardMapper
                .mapCard(cardResponse, marqetaRepository.getCardLimits(cardResponse.getCardProductToken())))
            .filter(cardItem -> (ids == null || ids.contains(cardItem.getId())))
            .filter(cardItem -> (status == null || status.contains(cardItem.getStatus())))
            .filter(cardItem -> (types == null || types.contains(cardItem.getType())))
            .collect(Collectors.toList());
    }

    public CardItem getCard(String id) {
        CardResponse cardResponse = marqetaRepository.getCardDetails(id);
        return cardMapper.mapCard(cardResponse, marqetaRepository.getCardLimits(cardResponse.getCardProductToken()));
    }

    public CardItem postLockStatus(String id, LockStatusPost lockStatusPost) {
        marqetaRepository.updateCard(id, cardMapper.mapUpdateCardRequestForLockStatus(id, lockStatusPost));
        return getCard(id);
    }

    public CardItem activateCard(String id, ActivatePost activatePost) {
        validateCvv(id, activatePost.getToken());
        marqetaRepository.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.ACTIVE));
        marqetaRepository.updateCard(id, cardMapper.mapUpdateCardRequestForActivation(id));
        return getCard(id);
    }

    public CardItem resetPin(String id, ResetPinPost resetPinPost) {
        validateCvv(id, resetPinPost.getToken());
        marqetaRepository.updatePin(new PinRequest()
            .controlToken(
                marqetaRepository.getPinControlToken(new ControlTokenRequest().cardToken(id)).getControlToken())
            .pin(resetPinPost.getPin()));
        return getCard(id);
    }

    private void validateCvv(String cardToken, String cvv) {
        if (!cvv.equals(marqetaRepository.getCardCvv(cardToken).getCvvNumber())) {
            throw new BadRequestException()
                .withErrors(Collections.singletonList(new Error().withMessage("cvv is incorrect")));
        }
    }

    public CardItem requestReplacement(String id, RequestReplacementPost requestReplacementPost) {
        marqetaRepository.postCardTransitions(cardMapper.mapCardTransitionRequest(id, StateEnum.SUSPENDED));
        marqetaRepository.updateCard(id, cardMapper.mapUpdateCardRequestForReplacement(id, requestReplacementPost));
        return getCard(id);
    }

    public CardItem changeLimits(String id, List<ChangeLimitsPostItem> changeLimitsPostItem) {
        changeLimitsPostItem.forEach(item -> marqetaRepository
            .updateCardLimits(item.getId(), new VelocityControlUpdateRequest().amountLimit(item.getAmount())));
        return getCard(id);
    }
}
