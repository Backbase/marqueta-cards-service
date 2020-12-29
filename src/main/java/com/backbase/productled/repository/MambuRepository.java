package com.backbase.productled.repository;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.mambu.clients.api.DepositAccountsApi;
import com.backbase.mambu.clients.model.Card;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

@Service
@Slf4j
@AllArgsConstructor
public class MambuRepository {

    private final DepositAccountsApi depositAccountsApi;

    public List<Card> getCards(String accountId) {
        List<Card> cards;
        try {
            cards = depositAccountsApi.getAllCards(accountId);
        } catch (HttpClientErrorException e) {
            switch (e.getStatusCode()) {
                case NOT_FOUND:
                    log.error("Deposit Account {} not found in Mambu: {}", accountId, e.getMessage());
                    throw new NotFoundException("Deposit Account not found in Mambu", e);
                case BAD_REQUEST:
                    log.error("Bad Request: {}", e.getMessage(), e);
                    throw new BadRequestException(
                        "Bad request retrieving Deposit Account from Mambu: " + e.getMessage(), e);
                default:
                    log.error("Unexpected error retrieving Deposit Account from Mambu: {}", e.getMessage(), e);
                    throw new InternalServerErrorException(
                        "Unexpected error retrieving Deposit Account from Mambu: " + e.getMessage(), e);
            }
        }
        return cards;
    }

}
