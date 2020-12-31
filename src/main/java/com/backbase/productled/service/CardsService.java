package com.backbase.productled.service;

import com.backbase.mambu.clients.model.Card;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.ArrangementRepository;
import com.backbase.productled.repository.MambuRepository;
import com.backbase.productled.repository.MarqetaRepository;
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
}
