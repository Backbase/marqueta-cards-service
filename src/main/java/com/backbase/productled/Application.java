package com.backbase.productled;

import com.backbase.productled.productsummary.listener.client.v2.productsummary.ProductsummaryProductSummaryClientHttpImplAutoConfiguration;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@EnableDiscoveryClient
@Import(value = {com.backbase.buildingblocks.backend.communication.http.HttpCommunicationConfiguration.class, ProductsummaryProductSummaryClientHttpImplAutoConfiguration.class})
public class Application extends SpringBootServletInitializer {

    public static void main(final String[] args) {
        SpringApplication.run(Application.class, args);
    }

}