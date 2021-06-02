package com.backbase.productled.config;


import static com.backbase.buildingblocks.communication.http.HttpCommunicationConfiguration.INTERCEPTORS_ENABLED_HEADER;
import static com.backbase.buildingblocks.communication.http.HttpCommunicationConfiguration.INTER_SERVICE_REST_TEMPLATE_BEAN_NAME;

import com.backbase.dbs.user.manager.api.service.ApiClient;
import com.backbase.dbs.user.manager.api.service.v2.UserManagementApi;
import javax.validation.constraints.Pattern;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Setter
@Configuration
@ConfigurationProperties(prefix = "backbase.usermanager.client.openapi")
public class UserManagerRestClientConfiguration {

    @Value("${backbase.communication.http.default-scheme:http}")
    @Pattern(regexp = "https?")
    private String scheme;
    private String serviceId = "user-manager";
    private String baseUri = "";
    private RestTemplate restTemplate;

    public UserManagerRestClientConfiguration(
        @Qualifier(INTER_SERVICE_REST_TEMPLATE_BEAN_NAME) RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Bean("userManagerApiClient")
    public ApiClient apiClient() {
        var apiClient = new ApiClient(restTemplate);
        apiClient.setBasePath(getBasePath());
        apiClient.addDefaultHeader(INTERCEPTORS_ENABLED_HEADER, Boolean.TRUE.toString());
        return apiClient;
    }

    @Bean
    public UserManagementApi userManagementApiClient() {
        return new UserManagementApi(apiClient());
    }

    private String getBasePath() {
        return UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(serviceId)
            .path(baseUri)
            .toUriString();
    }

}
