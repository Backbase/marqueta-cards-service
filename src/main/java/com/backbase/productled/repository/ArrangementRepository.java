package com.backbase.productled.repository;

import static com.backbase.productled.utils.CardConstants.BUSINESS_FUNCTION;
import static com.backbase.productled.utils.CardConstants.PRIVILEGE;
import static com.backbase.productled.utils.CardConstants.RESOURCE_NAME;

import com.backbase.presentation.productsummary.listener.client.v2.productsummary.GetArrangementsByBusinessFunctionQueryParameters;
import com.backbase.presentation.productsummary.listener.client.v2.productsummary.ProductsummaryProductSummaryClient;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ArrangementsByBusinessFunctionGetResponseBody;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ArrangementRepository {

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