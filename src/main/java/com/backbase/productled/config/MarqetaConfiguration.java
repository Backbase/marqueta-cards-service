package com.backbase.productled.config;

import com.backbase.marqeta.clients.ApiClient;
import com.backbase.marqeta.clients.api.CardTransitionsApi;
import com.backbase.marqeta.clients.api.CardsApi;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MarqetaConfiguration {

    @Bean
    public CardsApi cardsApi(@Qualifier("marqetaApiClient") ApiClient apiClient) {
        return new CardsApi(apiClient);
    }

    @Bean
    public CardTransitionsApi cardTransitionsApi(@Qualifier("marqetaApiClient") ApiClient apiClient) {
        return new CardTransitionsApi(apiClient);
    }

    @Bean
    @Qualifier("marqetaApiClient")
    public ApiClient marqetaApiClient(MarqetaConfigurationProperties marqetaConfigurationProperties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setUsername(marqetaConfigurationProperties.getUsername());
        apiClient.setPassword(marqetaConfigurationProperties.getPassword());
        apiClient.setBasePath(marqetaConfigurationProperties.getBaseUrl());
        return apiClient;
    }

}
