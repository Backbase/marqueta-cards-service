package com.backbase.productled.repository;

import com.backbase.dbs.arrangement.api.service.v2.ProductSummaryApi;
import com.backbase.dbs.arrangement.api.service.v2.model.ProductSummaryItem;
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

    private final ProductSummaryApi productSummaryApi;

    public List<String> getExternalArrangementIds() {

        return Objects.requireNonNull(productSummaryApi.getArrangementsByBusinessFunction(
            BUSINESS_FUNCTION, RESOURCE_NAME, PRIVILEGE, null, null, null, null, null, null, null, null, null, null,
            null, null, null, null, null, null, null, null)).stream()
            .map(ProductSummaryItem::getBBAN)
            .collect(Collectors.toList());
    }


}
