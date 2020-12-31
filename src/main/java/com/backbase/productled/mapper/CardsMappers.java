package com.backbase.productled.mapper;

import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import java.util.Collections;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardsMappers {

    String LOCK_STATUS = "lockStatus";

    @Mapping(target = "id", source = "cardResponse.token")
    @Mapping(target = "brand", expression = "java(cardResponse.getMetadata().get(\"brand\"))")
    @Mapping(target = "type", expression = "java(cardResponse.getMetadata().get(\"type\"))")
    @Mapping(target = "subType", expression = "java(cardResponse.getMetadata().get(\"subType\"))")
    @Mapping(target = "holder", expression = "java(new com.backbase.presentation.card.rest.spec.v2.cards.CardHolder().name(cardResponse.getMetadata().get(\"cardHolderName\")))")
    @Mapping(target = "name", expression = "java(cardResponse.getMetadata().get(\"name\"))")
    @Mapping(target = "status", expression = "java(cardResponse.getState() == com.backbase.marqeta.clients.model.CardResponse.StateEnum.ACTIVE ? \"Active\" : \"Inactive\")")
    @Mapping(target = "lockStatus", expression = "java(com.backbase.presentation.card.rest.spec.v2.cards.LockStatus.fromValue(cardResponse.getMetadata().get(\"lockStatus\")))")
    @Mapping(target = "replacement", expression = "java(new com.backbase.presentation.card.rest.spec.v2.cards.Replacement().status(cardResponse.getMetadata().get(\"replacementStatus\")))")
    @Mapping(target = "expiryDate", expression = "java(new com.backbase.presentation.card.rest.spec.v2.cards.YearMonth().year(String.valueOf(cardResponse.getExpirationTime().getYear())).month(String.valueOf(cardResponse.getExpirationTime().getMonthValue())))")
    @Mapping(target = "currency", expression = "java(cardResponse.getMetadata().get(\"currency\"))")
    @Mapping(target = "maskedNumber", source = "cardResponse.lastFour")
    @Mapping(target = "delivery", ignore = true)
    @Mapping(target = "limits", ignore = true)
    @Mapping(target = "additions", ignore = true)
    CardItem mapCard(CardResponse cardResponse);

    @Mapping(target = "token", expression = "java(java.util.UUID.randomUUID().toString())")
    @Mapping(target = "cardToken", source = "id")
    @Mapping(target = "channel", expression = "java(CardTransitionRequest.ChannelEnum.API)")
    @Mapping(target = "state", expression = "java(CardTransitionRequest.StateEnum.ACTIVE)")
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "reasonCode", ignore = true)
    @Mapping(target = "validations", ignore = true)
    CardTransitionRequest mapCardTransitionRequest(String id);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "metadata", source = "lockStatusPost", qualifiedByName = "metadata")
    @Mapping(target = "userToken", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    CardUpdateRequest mapCardLockStatusRequest(String token, LockStatusPost lockStatusPost);

    @Named("metadata")
    default Map<String, String> getMetadata(LockStatusPost lockStatusPost) {
        return Collections.singletonMap(LOCK_STATUS, lockStatusPost.getLockStatus().toString());
    }

}
