package com.backbase.productled.service;

import com.backbase.mambu.clients.model.Card;
import com.backbase.presentation.card.rest.spec.v2.cards.IdlockstatusPostRequestBody;
import com.backbase.presentation.card.spec.v2.cards.CardItem;
import com.backbase.productled.mapper.CardMapper;
import com.backbase.productled.repository.ArrangementRepository;
import com.backbase.productled.repository.MambuRepository;
import com.backbase.productled.repository.MarqetaRepository;
import java.util.Arrays;
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

    private final CardMapper cardMapper;

    public List<CardItem> getCards(String[] ids, String[] status, String[] types) {

        return arrangementRepository.getExternalArrangementIds().stream()
            .map(mambuRepository::getCards)
            .collect(Collectors.toList())
            .stream().flatMap(List::stream)
            .collect(Collectors.toList()).stream()
            .map(Card::getReferenceToken)
            .map(marqetaRepository::getCardDetails)
            .map(cardMapper::mapCard)
            .filter(cardItem -> (ids == null || Arrays.asList(ids).contains(cardItem.getId())))
            .filter(cardItem -> (status == null || Arrays.asList(status).contains(cardItem.getStatus())))
            .filter(cardItem -> (types == null || Arrays.asList(types).contains(cardItem.getType())))
            .collect(Collectors.toList());
    }

    public CardItem postLockStatus(String id, IdlockstatusPostRequestBody idlockstatusPostRequestBody) {
        marqetaRepository.postCardTransitions(cardMapper
            .mapCardTransitionRequest(id, idlockstatusPostRequestBody));
        return cardMapper.mapCard(marqetaRepository.getCardDetails(id));
    }
}
