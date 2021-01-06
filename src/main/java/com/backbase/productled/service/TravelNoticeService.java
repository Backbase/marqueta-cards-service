package com.backbase.productled.service;

import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.MarqetaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TravelNoticeService {

    private final MarqetaRepository marqetaRepository;

    private final CardsMappers cardsMappers;

    private final CardsService cardsService;

    private final ObjectMapper objectMapper;

    public TravelNotice createTravelNotice(TravelNotice travelNotice) {
        cardsService.getCardsLinkedToAccount().stream()
            .map(CardResponse::getUserToken)
            .map(marqetaRepository::getCardHolder)
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> {
                try {
                    return req.metadata(Collections.singletonMap(String.format("travelnotice-%s", travelNotice.getId()),
                        Arrays.toString(objectMapper.writeValueAsBytes(travelNotice))));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return req;
            })
            .forEach(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    public void deleteTravelNoticeById(String id) {
        cardsService.getCardsLinkedToAccount().stream()
            .map(CardResponse::getUserToken)
            .map(marqetaRepository::getCardHolder)
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> req.metadata(Collections.singletonMap(String.format("travelnotice-%s", id), null)))
            .forEach(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
    }

    @SneakyThrows
    public TravelNotice getTravelNoticeById(String id) {
        return cardsService.getCardsLinkedToAccount().stream()
            .map(CardResponse::getUserToken)
            .map(marqetaRepository::getCardHolder)
            .map(response -> response.getMetadata()
                .get(String.format("tarvelnotice-%s", id)))
            .filter(Objects::nonNull)
            .map(content -> {
                try {
                    return objectMapper.readValue(content, TravelNotice.class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }).findFirst().orElseThrow(NotFoundException::new);
    }

    public List<TravelNotice> getTravelNotices() {
        return cardsService.getCardsLinkedToAccount().stream()
            .map(CardResponse::getUserToken)
            .findFirst()
            .map(marqetaRepository::getCardHolder)
            .map(UserCardHolderResponse::getMetadata)
            .map(map -> map.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith("tarvelnotice-"))
                .map(entry -> {
                    try {
                        return objectMapper.readValue(entry.getValue(), TravelNotice.class);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return null;
                })
                .collect(Collectors.toList())
            ).get();
    }

    public TravelNotice updateTravelNotice(String id) {
        return null;
    }
}
