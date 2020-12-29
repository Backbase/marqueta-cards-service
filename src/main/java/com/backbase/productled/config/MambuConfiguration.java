package com.backbase.productled.config;

import com.backbase.mambu.clients.ApiClient;
import com.backbase.mambu.clients.api.DepositAccountsApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Initialize and inject beans for Mambu clients using configuration properties
 */
@Configuration
public class MambuConfiguration {

    @Bean
    public DepositAccountsApi depositAccountsApi(ApiClient apiClient) {
        return new DepositAccountsApi(apiClient);
    }

    @Bean
    public ApiClient apiClient(MambuConfigurationProperties mambuConfigurationProperties) {
        ApiClient apiClient = new ApiClient();
        apiClient.setUsername(mambuConfigurationProperties.getUsername());
        apiClient.setPassword(mambuConfigurationProperties.getPassword());
        apiClient.setBasePath(mambuConfigurationProperties.getBaseUrl());
        apiClient.setDebugging(true);
        return apiClient;
    }
}