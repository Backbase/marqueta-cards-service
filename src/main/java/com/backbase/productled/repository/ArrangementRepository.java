package com.backbase.productled.repository;

import com.backbase.productled.productsummary.listener.client.v2.productsummary.GetArrangementsByBusinessFunctionQueryParameters;
import com.backbase.productled.productsummary.listener.client.v2.productsummary.ProductsummaryProductSummaryClient;
import com.backbase.productled.productsummary.rest.spec.v2.productsummary.ArrangementsByBusinessFunctionGetResponseBody;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArrangementRepository {

    private static final String BUSINESS_FUNCTION = "Product Summary";
    private static final String RESOURCE_NAME = "Product Summary";
    private static final String PRIVILEGE = "view";

    private ProductsummaryProductSummaryClient productsummaryProductSummaryClient;

    public List<String> getExternalArrangementIds() {

        return Objects.requireNonNull(productsummaryProductSummaryClient.getArrangementsByBusinessFunction(
            new GetArrangementsByBusinessFunctionQueryParameters()
                .withBusinessFunction(BUSINESS_FUNCTION)
                .withResourceName(RESOURCE_NAME)
                .withPrivilege(PRIVILEGE)).getBody()).stream()
            .map(ArrangementsByBusinessFunctionGetResponseBody::getBBAN)
            .collect(Collectors.toList());
    }


}
