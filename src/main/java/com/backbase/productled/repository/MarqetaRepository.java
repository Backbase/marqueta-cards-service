package com.backbase.productled.repository;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.api.CardTransitionsApi;
import com.backbase.marqeta.clients.api.CardsApi;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardTransitionResponse;
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

    public CardResponse getCardDetails(String token) {

        try {
            return cardsApi.getCardsToken(token, null, null);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    log.error("Card token {} not found in Marqeta: {}", token, e.getMessage());
                    throw new NotFoundException("Card token not found in Marqeta", e);
                case BAD_REQUEST:
                    log.error("Bad Request: {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request retrieving Card token from Marqeta: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error retrieving Card token from Marqeta: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error retrieving Card token from Marqeta: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("exception ", e);
            throw new InternalServerErrorException(
                "Unexpected error retrieving Card token from Marqeta: " + e.getMessage(), e);
        }

    }

    public CardTransitionResponse postCardTransitions(CardTransitionRequest cardTransitionRequest) {
        try {
            return cardTransitionsApi.postCardtransitions(cardTransitionRequest);
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
}
