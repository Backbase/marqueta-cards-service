package com.backbase.productled.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Configuration properties used to interact with Mambu
 */
@Component
@ConfigurationProperties(prefix = "mambu")
@Data
public class MambuConfigurationProperties {

    private String baseUrl;
    private String username;
    private String password;
}
