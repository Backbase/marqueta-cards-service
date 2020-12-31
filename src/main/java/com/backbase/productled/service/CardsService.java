package com.backbase.productled.service;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.Error;
import com.backbase.mambu.clients.model.Card;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.ResetPinPost;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.ArrangementRepository;
import com.backbase.productled.repository.MambuRepository;
import com.backbase.productled.repository.MarqetaRepository;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
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
            .map(cardMapper::mapCard)
            .filter(cardItem -> (ids == null || ids.contains(cardItem.getId())))
            .filter(cardItem -> (status == null || status.contains(cardItem.getStatus())))
            .filter(cardItem -> (types == null || types.contains(cardItem.getType())))
            .collect(Collectors.toList());
    }

    public CardItem getCard(String id) {
        return cardMapper.mapCard(marqetaRepository.getCardDetails(id));
    }

    public CardItem postLockStatus(String id, LockStatusPost lockStatusPost) {
        marqetaRepository.updateCard(id, cardMapper.mapCardLockStatusRequest(id, lockStatusPost));
        return getCard(id);
    }

    public CardItem activateCard(String id) {
        marqetaRepository.postCardTransitions(cardMapper.mapCardTransitionRequest(id));
        return getCard(id);
    }

    public ResponseEntity<CardItem> resetPin(String id, ResetPinPost resetPinPost) {
        if (!resetPinPost.getToken().equals(marqetaRepository.getCardCvv(id).getCvvNumber())) {
            throw new BadRequestException()
                .withErrors(Collections.singletonList(new Error().withMessage("cvv is incorrect")));
        }
        marqetaRepository.updatePin(new PinRequest()
            .controlToken(
                marqetaRepository.getPinControlToken(new ControlTokenRequest().cardToken(id)).getControlToken())
            .pin(resetPinPost.getPin()));
        return ResponseEntity.ok(getCard(id));
    }
}
