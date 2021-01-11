package com.backbase.productled.utils;

import static com.backbase.productled.utils.CardConstants.TRAVEL_NOTICE_REGEX;

import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.repository.MarqetaRepository;
import com.backbase.productled.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TravelNoticeUtil {

    private final ObjectMapper objectMapper;

    private final MarqetaRepository marqetaRepository;

    private final UserRepository userRepository;

    public UserCardHolderUpdateModel getUserCardHolderUpdateModel(String id, UserCardHolderUpdateModel req,
        TravelNotice travelNotice) {
        try {
            return req.metadata(Collections.singletonMap(String.format(TRAVEL_NOTICE_REGEX, id),
                objectMapper.writeValueAsString(travelNotice.id(id))));
        } catch (IOException e) {
            throw new InternalServerErrorException().withMessage("Error while parsing travel notice");
        }
    }

    public TravelNotice getTravelNotice(String value) {
        try {
            return objectMapper.readValue(value, TravelNotice.class);
        } catch (IOException e) {
            throw new InternalServerErrorException().withMessage("Error while parsing travel notice");
        }
    }

    public Optional<UserCardHolderResponse> getMarqetaUserToken() {
        return Optional.ofNullable(userRepository.getMarqetaUserToken())
            .map(marqetaRepository::getCardHolder);
    }

}
