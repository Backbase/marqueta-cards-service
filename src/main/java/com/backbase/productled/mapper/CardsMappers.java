package com.backbase.productled.mapper;

import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestReplacementPost;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface CardsMappers {

    String LOCK_STATUS = "lockStatus";
    String REPLACEMENT_REASON = "replacementReason";
    String REPLACEMENT_STATUS = "replacementStatus";
    String NOT_UNDER_REPLACEMENT = "NotUnderReplacement";
    String UNDER_REPLACEMENT = "UnderReplacement";

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
    @Mapping(target = "state", source = "state")
    @Mapping(target = "reason", ignore = true)
    @Mapping(target = "reasonCode", ignore = true)
    @Mapping(target = "validations", ignore = true)
    CardTransitionRequest mapCardTransitionRequest(String id, StateEnum state);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "metadata", source = "lockStatusPost", qualifiedByName = "statusMetadata")
    @Mapping(target = "userToken", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    CardUpdateRequest mapUpdateCardRequestForLockStatus(String token, LockStatusPost lockStatusPost);

    @Named("statusMetadata")
    default Map<String, String> getStatusMetadata(LockStatusPost lockStatusPost) {
        return Collections.singletonMap(LOCK_STATUS, lockStatusPost.getLockStatus().toString());
    }

    @Mapping(target = "token", source = "token")
    @Mapping(target = "metadata", source = "requestReplacementPost", qualifiedByName = "replacementMetaData")
    @Mapping(target = "userToken", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    CardUpdateRequest mapUpdateCardRequestForReplacement(String id, RequestReplacementPost requestReplacementPost);

    @Named("replacementMetaData")
    default Map<String, String> getReplacementMetaData(RequestReplacementPost requestReplacementPost) {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put(REPLACEMENT_STATUS, UNDER_REPLACEMENT);
        metaData.put(REPLACEMENT_REASON, requestReplacementPost.getReplacementReason());
        return metaData;
    }

    @Mapping(target = "token", source = "token")
    @Mapping(target = "metadata", qualifiedByName = "activationMetaData")
    @Mapping(target = "userToken", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    CardUpdateRequest mapUpdateCardRequestForActivation(String id);

    @Named("activationMetaData")
    default Map<String, String> getActivationMetaData() {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put(REPLACEMENT_STATUS, NOT_UNDER_REPLACEMENT);
        metaData.put(REPLACEMENT_REASON, null);
        return metaData;
    }
}
