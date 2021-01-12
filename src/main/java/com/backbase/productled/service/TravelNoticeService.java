package com.backbase.productled.service;

import static com.backbase.productled.utils.CardConstants.TRAVEL_NOTICE;
import static com.backbase.productled.utils.CardConstants.TRAVEL_NOTICE_REGEX;

import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.repository.MarqetaRepository;
import com.backbase.productled.utils.TravelNoticeUtil;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TravelNoticeService {

    private final TravelNoticeUtil travelNoticeUtil;

    private final MarqetaRepository marqetaRepository;

    private final CardsMappers cardsMappers;

    public TravelNotice createTravelNotice(TravelNotice travelNotice) {
        travelNoticeUtil.getMarqetaUserToken()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> travelNoticeUtil.getUserCardHolderUpdateModel(UUID.randomUUID().toString(), req, travelNotice))
            .ifPresent(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

    public void deleteTravelNoticeById(String id) {
        travelNoticeUtil.getMarqetaUserToken()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id), null)))
            .ifPresent(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
    }

    public TravelNotice getTravelNoticeById(String id) {
        return travelNoticeUtil.getMarqetaUserToken()
            .filter(response -> response.getMetadata() != null)
            .map(response -> response.getMetadata()
                .get(String.format(TRAVEL_NOTICE_REGEX, id)))
            .map(travelNoticeUtil::getTravelNotice)
            .orElseThrow(NotFoundException::new);
    }

    public List<TravelNotice> getTravelNotices() {
        return travelNoticeUtil.getMarqetaUserToken()
            .map(UserCardHolderResponse::getMetadata)
            .map(map -> map.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(TRAVEL_NOTICE))
                .map(entry -> travelNoticeUtil.getTravelNotice(entry.getValue()))
                .collect(Collectors.toList())
            ).orElse(new ArrayList<>());
    }

    public TravelNotice updateTravelNotice(String id, TravelNotice travelNotice) {
        travelNoticeUtil.getMarqetaUserToken()
            .map(cardsMappers::mapUpdateCardHolderRequest)
            .map(req -> travelNoticeUtil.getUserCardHolderUpdateModel(id, req, travelNotice))
            .ifPresent(model -> marqetaRepository.updateCardHolder(model.getToken(), model));
        return travelNotice;
    }

}
