package com.backbase.productled.service;

import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.MarqetaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TravelNoticeService {

    private static final String TRAVEL_NOTICE_REGEX = "travelnotice-%s";

    private static final String TRAVEL_NOTICE = "travelnotice";

    private final MarqetaRepository marqetaRepository;

    private final CardsMappers cardsMappers;

    private final CardsService cardsService;

    private final ObjectMapper objectMapper;

    public TravelNotice createTravelNotice(TravelNotice travelNotice) {
        getUserCardHolder()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> getUserCardHolderUpdateModel(UUID.randomUUID().toString(), req, travelNotice))
            .map(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    public void deleteTravelNoticeById(String id) {
        getUserCardHolder()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id), null)))
            .map(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
    }

    public TravelNotice getTravelNoticeById(String id) {
        return getUserCardHolder()
            .filter(response -> response.getMetadata()!= null)
            .map(response -> response.getMetadata()
                .get(String.format(TRAVEL_NOTICE_REGEX, id)))
            .map(this::getTravelNotice)
            .orElseThrow(NotFoundException::new);
    }

    public List<TravelNotice> getTravelNotices() {
        return getUserCardHolder()
            .map(UserCardHolderResponse::getMetadata)
            .map(map -> map.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(TRAVEL_NOTICE))
                .map(entry -> getTravelNotice(entry.getValue()))
                .collect(Collectors.toList())
            ).orElse(new ArrayList<>());
    }

    public TravelNotice updateTravelNotice(String id, TravelNotice travelNotice) {
        getUserCardHolder()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> getUserCardHolderUpdateModel(id, req, travelNotice))
            .map(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    private Optional<UserCardHolderResponse> getUserCardHolder() {
        return cardsService.getCardsLinkedToAccount().stream()
            .map(CardResponse::getUserToken)
            .findFirst()
            .map(marqetaRepository::getCardHolder);
    }

    private UserCardHolderUpdateModel getUserCardHolderUpdateModel(String id, UserCardHolderUpdateModel req,
        TravelNotice travelNotice) {
        try {
            return req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id),
                objectMapper.writeValueAsString(travelNotice)));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return req;
    }

    private TravelNotice getTravelNotice(String value) {
        try {
            return objectMapper.readValue(value, TravelNotice.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
