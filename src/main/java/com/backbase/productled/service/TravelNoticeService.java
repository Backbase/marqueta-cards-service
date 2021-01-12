package com.backbase.productled.service;


import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.MarqetaRepository;
import com.backbase.productled.repository.UserRepository;
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

    private final ObjectMapper objectMapper;

    private final UserRepository userRepository;

    public TravelNotice createTravelNotice(TravelNotice travelNotice) {
        getMarqetaUserToken()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> getUserCardHolderUpdateModel(UUID.randomUUID().toString(), req, travelNotice))
            .ifPresent(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    public void deleteTravelNoticeById(String id) {
        getMarqetaUserToken()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id), null)))
            .ifPresent(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
    }

    public TravelNotice getTravelNoticeById(String id) {
        return getMarqetaUserToken()
            .filter(response -> response.getMetadata() != null)
            .map(response -> response.getMetadata()
                .get(String.format(TRAVEL_NOTICE_REGEX, id)))
            .map(this::getTravelNotice)
            .orElseThrow(NotFoundException::new);
    }

    public List<TravelNotice> getTravelNotices() {
        return getMarqetaUserToken()
            .map(UserCardHolderResponse::getMetadata)
            .map(map -> map.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(TRAVEL_NOTICE))
                .map(entry -> getTravelNotice(entry.getValue()))
                .collect(Collectors.toList())
            ).orElse(new ArrayList<>());
    }

    public TravelNotice updateTravelNotice(String id, TravelNotice travelNotice) {
        getMarqetaUserToken()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> getUserCardHolderUpdateModel(id, req, travelNotice))
            .ifPresent(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    private com.backbase.marqeta.clients.model.UserCardHolderUpdateModel getUserCardHolderUpdateModel(String id,
        com.backbase.marqeta.clients.model.UserCardHolderUpdateModel req,
        TravelNotice travelNotice) {
        try {
            return req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id),
                objectMapper.writeValueAsString(travelNotice.id(id))));
        } catch (IOException e) {
            throw new InternalServerErrorException().withMessage("Error while parsing travel notice");
        }
    }

    private TravelNotice getTravelNotice(String value) {
        try {
            return objectMapper.readValue(value, TravelNotice.class);
        } catch (IOException e) {
            throw new InternalServerErrorException().withMessage("Error while parsing travel notice");
        }
    }

    private Optional<UserCardHolderResponse> getMarqetaUserToken() {
        return Optional.ofNullable(userRepository.getMarqetaUserToken())
            .map(marqetaRepository::getCardHolder);
    }

}
