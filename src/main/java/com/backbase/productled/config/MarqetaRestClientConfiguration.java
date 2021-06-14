package com.backbase.productled.config;

import com.backbase.marqeta.clients.ApiClient;
import com.backbase.marqeta.clients.api.CardTransitionsApi;
import com.backbase.marqeta.clients.api.CardsApi;
import com.backbase.marqeta.clients.api.PinsApi;
import com.backbase.marqeta.clients.api.UsersApi;
import com.backbase.marqeta.clients.api.VelocityControlsApi;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "marqeta")
@Setter
public class MarqetaRestClientConfiguration {

    private String baseUrl;
    private String username;
    private String password;

    @Bean
    public CardsApi cardsApi() {
        return new CardsApi(marqetaApiClient());
    }

    @Bean
    public PinsApi pinsApi() {
        return new PinsApi(marqetaApiClient());
    }

    @Bean
    public UsersApi usersApi() {
        return new UsersApi(marqetaApiClient());
    }

    @Bean
    public VelocityControlsApi velocityControlsApi() {
        return new VelocityControlsApi(marqetaApiClient());
    }

    @Bean
    public CardTransitionsApi cardTransitionsApi() {
        return new CardTransitionsApi(marqetaApiClient());
    }

    @Bean
    @Qualifier("marqetaApiClient")
    public ApiClient marqetaApiClient() {
        var apiClient = new ApiClient();
        apiClient.setUsername(username);
        apiClient.setPassword(password);
        apiClient.setBasePath(baseUrl);
        apiClient.setDebugging(true);
        return apiClient;
    }

}
