package com.backbase.productled.repository;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.api.CardTransitionsApi;
import com.backbase.marqeta.clients.api.CardsApi;
import com.backbase.marqeta.clients.api.PinsApi;
import com.backbase.marqeta.clients.api.VelocityControlsApi;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.ControlTokenResponse;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.marqeta.clients.model.VelocityControlListResponse;
import com.backbase.marqeta.clients.model.VelocityControlResponse;
import com.backbase.marqeta.clients.model.VelocityControlUpdateRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@AllArgsConstructor
@Slf4j
public class MarqetaRepository {

    private final CardsApi cardsApi;

    private final CardTransitionsApi cardTransitionsApi;

    private final PinsApi pinsApi;

    private final VelocityControlsApi velocityControlsApi;

    public CardResponse getCardDetails(String token) {
        try {
            return cardsApi.getCardsToken(token, null, null);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    log.error("Card token {} not found in Marqeta: {}", token, e.getMessage());
                    throw new NotFoundException("Card token not found in Marqeta", e);
                case BAD_REQUEST:
                    log.error("Bad Request while retrieving Card token from Marqeta : {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request retrieving Card token from Marqeta: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error retrieving Card token from Marqeta: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error retrieving Card token from Marqeta: " + e.getMessage(), e);
            }
        }
    }

    public CardResponse updateCard(String token, CardUpdateRequest cardUpdateRequest) {
        try {
            return cardsApi.putCardsToken(token, cardUpdateRequest);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    log.error("Card token {} not found in Marqeta: {}", token, e.getMessage());
                    throw new NotFoundException("Card token not found in Marqeta", e);
                case BAD_REQUEST:
                    log.error("Bad Request: {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request while updating Card  in Marqeta: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error retrieving Card token from Marqeta: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error while updating Card in Marqeta: " + e.getMessage(), e);
            }
        }
    }

    public void postCardTransitions(CardTransitionRequest cardTransitionRequest) {
        try {
            cardTransitionsApi.postCardtransitions(cardTransitionRequest);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    log.error("cardTransition not found in Marqeta: {}", e.getMessage());
                    throw new NotFoundException("cardTransition not found in Marqeta", e);
                case BAD_REQUEST:
                    log.error("Bad Request: {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request while posting cardTransition to Marqeta: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error while posting from Marqeta: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error from Marqeta while posting cardTransition : " + e.getMessage(), e);
            }
        }
    }

    public CardResponse getCardCvv(String token) {
        try {
            return cardsApi.getCardsTokenShowpan(token, null, true);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    log.error("Card token {} not found in Marqeta: {}", token, e.getMessage());
                    throw new NotFoundException("Card token not found in Marqeta", e);
                case BAD_REQUEST:
                    log.error("Bad Request while retrieving Card cvv from Marqeta : {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request retrieving Card cvv from Marqeta: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error retrieving Card token from Marqeta: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error retrieving Card cvv token from Marqeta: " + e.getMessage(), e);
            }
        }
    }

    public ControlTokenResponse getPinControlToken(ControlTokenRequest controlTokenRequest) {
        try {
            return pinsApi.postPinsControltoken(controlTokenRequest);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    log.error("Card token {} not found in Marqeta: {}", controlTokenRequest.getCardToken(),
                        e.getMessage());
                    throw new NotFoundException("Card token not found in Marqeta", e);
                case BAD_REQUEST:
                    log.error("Bad Request while requesting PinControlToken from Marqeta : {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request requesting PinControlToken from Marqeta: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error while requesting PinControlToken from Marqeta: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error while requesting PinControlToken from Marqeta: " + e.getMessage(), e);
            }
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
        return velocityControlsApi.getVelocitycontrols(cardProductToken, null, null, null, null, null);
    }

    public VelocityControlResponse updateCardLimits(String token, VelocityControlUpdateRequest velocityControlUpdateRequest) {
        return velocityControlsApi.putVelocitycontrolsToken(token, velocityControlUpdateRequest);
    }
}
