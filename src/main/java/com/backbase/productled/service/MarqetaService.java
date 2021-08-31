package com.backbase.productled.service;

import com.backbase.buildingblocks.presentation.errors.ApiErrorException;
import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.api.CardTransitionsApi;
import com.backbase.marqeta.clients.api.CardsApi;
import com.backbase.marqeta.clients.api.PinsApi;
import com.backbase.marqeta.clients.api.UsersApi;
import com.backbase.marqeta.clients.api.VelocityControlsApi;
import com.backbase.marqeta.clients.model.CardListResponse;
import com.backbase.marqeta.clients.model.CardRequest;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.ControlTokenResponse;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.marqeta.clients.model.VelocityControlListResponse;
import com.backbase.marqeta.clients.model.VelocityControlResponse;
import com.backbase.marqeta.clients.model.VelocityControlUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

/**
 * Service executes Marquetas client methods and handles errors
 */
@Service
@AllArgsConstructor
@Slf4j
public class MarqetaService {

    private final CardsApi cardsApi;

    private final CardTransitionsApi cardTransitionsApi;

    private final PinsApi pinsApi;

    private final VelocityControlsApi velocityControlsApi;

    private final UsersApi usersApi;

    public CardResponse getCardDetails(String token) {
        try {
            return cardsApi.getCardsToken(token, null, null);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card", token);
        }
    }

    public CardListResponse getUserCards(String userToken) {
        try {
            return cardsApi.getCardsUserToken(userToken, null, null, null, null);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "User", userToken);
        }
    }

    public CardResponse createCard(CardRequest cardRequest) {
        try {
            return cardsApi.postCards(false, false, cardRequest);
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Bad Request while creating Card in Marqeta : {}", e.getMessage(), e);
                throw new BadRequestException(
                    "Bad request retrieving creating Card in Marqeta: " + e.getMessage(), e);
            }
            log.error("Unexpected error while creating Card in Marqeta: {}", e.getMessage(), e);
            throw new InternalServerErrorException(
                "Unexpected error while creating Card in Marqeta: " + e.getMessage(), e);
        }
    }

    public CardResponse updateCard(String token, CardUpdateRequest cardUpdateRequest) {
        try {
            return cardsApi.putCardsToken(token, cardUpdateRequest);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card", token);
        }
    }

    public void postCardTransitions(CardTransitionRequest cardTransitionRequest) {
        try {
            cardTransitionsApi.postCardtransitions(cardTransitionRequest);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card Transition", cardTransitionRequest.getCardToken());
        }
    }

    public CardResponse getCardCvv(String token) {
        try {
            return cardsApi.getCardsTokenShowpan(token, null, true);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card", token);
        }
    }

    public ControlTokenResponse getPinControlToken(ControlTokenRequest controlTokenRequest) {
        try {
            return pinsApi.postPinsControltoken(controlTokenRequest);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "ControlToken", controlTokenRequest.getCardToken());
        }
    }

    public void updatePin(PinRequest pinRequest) {
        try {
            pinsApi.putPins(pinRequest);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case PRECONDITION_FAILED:
                    log.error("PRECONDITION_FAILED while resetting pin in Marqeta : {}", e.getMessage(), e);
                    throw new BadRequestException("Invalid input(s): pin is weak");
                case NOT_FOUND:
                    log.error("Control token {} not found in Marqeta: {}", pinRequest.getControlToken(),
                        e.getMessage());
                    throw new NotFoundException("Control token not found in Marqeta", e);
                case BAD_REQUEST:
                    log.error("Bad Request while resetting pin in Marqeta : {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request while resetting pin in Marqeta: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error while resetting pin in Marqeta: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error while resetting pin in Marqeta: " + e.getMessage(), e);
            }
        }
    }

    public VelocityControlListResponse getCardLimits(String cardProductToken) {
        try {
            return velocityControlsApi.getVelocitycontrols(cardProductToken, null, null, null, null, null);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card limits", cardProductToken);
        }
    }

    public void updateCardLimits(String token, VelocityControlUpdateRequest velocityControlUpdateRequest) {
        try {
            velocityControlsApi.putVelocitycontrolsToken(token, velocityControlUpdateRequest);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card Limits", token);
        }
    }

    public VelocityControlResponse getCardLimitById(String token) {
        try {
            return velocityControlsApi.getVelocitycontrolsToken(token, null);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "VelocityControl", token);
        }
    }

    public UserCardHolderResponse getCardHolder(String token) {
        try {
            return usersApi.getUsersToken(token, null);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card Holder", token);
        }
    }

    public void updateCardHolder(String token, UserCardHolderUpdateModel userCardHolderUpdateModel) {
        try {
            usersApi.putUsersToken(token, userCardHolderUpdateModel);
        } catch (HttpClientErrorException e) {
            throw mapException(e, "Card holder", token);
        }
    }

    private ApiErrorException mapException(HttpClientErrorException ex, String errorSubject, String token) {
        switch (ex.getStatusCode()) {
            case NOT_FOUND:
                log.error("{} Token {} not found in Marqeta: {}", errorSubject, token, ex.getMessage());
                return new NotFoundException("Velocity controls Token not found in Marqeta", ex);
            case BAD_REQUEST:
                log.error("Bad Request while retrieving {} from Marqeta: {}", errorSubject, ex.getMessage());
                return new BadRequestException(
                    "Bad request while retrieving " + errorSubject + " from Marqeta: " + ex.getMessage(), ex);
            default:
                log.error("Unexpected error while retrieving {} from Marqeta: {}", errorSubject, ex.getMessage());
                return new InternalServerErrorException(
                    "Unexpected error while retrieving " + errorSubject + " from Marqeta: " + ex.getMessage(), ex);
        }
    }
}
