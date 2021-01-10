package com.backbase.productled.service;

import static com.backbase.productled.utils.CardConstants.TRAVEL_NOTICE;
import static com.backbase.productled.utils.CardConstants.TRAVEL_NOTICE_REGEX;

import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.MarqetaRepository;
import com.backbase.productled.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TravelNoticeService {

    private final MarqetaRepository marqetaRepository;

    private final CardsMappers cardsMappers;

    private final UserRepository userRepository;

    private final ObjectMapper objectMapper;

    public TravelNotice createTravelNotice(TravelNotice travelNotice) {
        getCardHolder()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> getUserCardHolderUpdateModel(UUID.randomUUID().toString(), req, travelNotice))
            .map(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    public void deleteTravelNoticeById(String id) {
        getCardHolder()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id), null)))
            .map(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
    }

    public TravelNotice getTravelNoticeById(String id) {
        return getCardHolder()
            .filter(response -> response.getMetadata() != null)
            .map(response -> response.getMetadata()
                .get(String.format(TRAVEL_NOTICE_REGEX, id)))
            .map(this::getTravelNotice)
            .orElseThrow(NotFoundException::new);
    }

    public List<TravelNotice> getTravelNotices() {
        return getCardHolder()
            .map(UserCardHolderResponse::getMetadata)
            .map(map -> map.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(TRAVEL_NOTICE))
                .map(entry -> getTravelNotice(entry.getValue()))
                .collect(Collectors.toList())
            ).orElse(new ArrayList<>());
    }

    public TravelNotice updateTravelNotice(String id, TravelNotice travelNotice) {
        getCardHolder()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> getUserCardHolderUpdateModel(id, req, travelNotice))
            .map(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    private Optional<UserCardHolderResponse> getCardHolder() {
        return Optional.ofNullable(userRepository.getMarqetaUserToken())
            .map(marqetaRepository::getCardHolder);
    }

    @SneakyThrows
    private UserCardHolderUpdateModel getUserCardHolderUpdateModel(String id, UserCardHolderUpdateModel req,
        TravelNotice travelNotice) {
        return req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id),
            objectMapper.writeValueAsString(travelNotice.id(id))));
    }

    @SneakyThrows
    private TravelNotice getTravelNotice(String value) {
        return objectMapper.readValue(value, TravelNotice.class);
    }
}
