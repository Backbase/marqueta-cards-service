package com.backbase.productled.config;

import static com.backbase.buildingblocks.backend.communication.http.HttpCommunicationConfiguration.INTERCEPTORS_ENABLED_HEADER;
import static com.backbase.buildingblocks.backend.communication.http.HttpCommunicationConfiguration.INTER_SERVICE_REST_TEMPLATE_BEAN_NAME;

import com.backbase.dbs.arrangement.api.service.ApiClient;
import com.backbase.dbs.arrangement.api.service.v2.ProductSummaryApi;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Setter
@Configuration
@ConfigurationProperties(prefix = "backbase.productsummary.client.openapi")
public class ProductSummaryRestClientConfiguration {

    private String baseUrl;
    private RestTemplate restTemplate;

    public ProductSummaryRestClientConfiguration(
        @Qualifier(INTER_SERVICE_REST_TEMPLATE_BEAN_NAME) RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Bean
    public ProductSummaryApi productSummaryApi() {
        return new ProductSummaryApi(apiClient());
    }

    @Bean("productSummaryApiClient")
    public ApiClient apiClient() {
        ApiClient apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(baseUrl);
        apiClient.addDefaultHeader(INTERCEPTORS_ENABLED_HEADER, Boolean.TRUE.toString());
        return apiClient;
    }

}
