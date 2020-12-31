package com.backbase.productled.config;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Setter
@Configuration
@ConfigurationProperties(prefix = "backbase.productsummary.client.openapi")
public class ProductSummaryRestClientConfiguration {

    /*private String baseUrl;
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
    }*/

}
