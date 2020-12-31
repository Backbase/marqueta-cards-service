package com.backbase.productled.config;

import com.backbase.mambu.clients.ApiClient;
import com.backbase.mambu.clients.api.DepositAccountsApi;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialize and inject beans for Mambu clients using configuration properties
 */
@Configuration
@ConfigurationProperties(prefix = "mambu")
@Setter
public class MambuRestClientConfiguration {

    private String baseUrl;
    private String username;
    private String password;

    @Bean
    public DepositAccountsApi depositAccountsApi() {
        return new DepositAccountsApi(mambuApiClient());
    }

    @Bean
    @Qualifier("mambuApiClient")
    public ApiClient mambuApiClient() {
        ApiClient apiClient = new ApiClient();
        apiClient.setUsername(username);
        apiClient.setPassword(password);
        apiClient.setBasePath(baseUrl);
        apiClient.setDebugging(true);
        return apiClient;
    }
}