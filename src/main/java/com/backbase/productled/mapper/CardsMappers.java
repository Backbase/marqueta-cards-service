package com.backbase.productled.mapper;

import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.marqeta.clients.model.VelocityControlListResponse;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestReplacementPost;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.map.ObjectMapper;
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
    String ATM = "ATM";
    String ONLINE = "ONLINE";

    @Mapping(target = "id", source = "cardResponse.token")
    @Mapping(target = "brand", expression = "java(cardResponse.getMetadata().get(\"brand\"))")
    @Mapping(target = "type", expression = "java(cardResponse.getMetadata().get(\"type\"))")
    @Mapping(target = "subType", expression = "java(cardResponse.getMetadata().get(\"subType\"))")
    @Mapping(target = "holder", expression = "java(new com.backbase.presentation.card.rest.spec.v2.cards.CardHolder().name(cardResponse.getMetadata().get(\"cardHolderName\")))")
    @Mapping(target = "name", expression = "java(cardResponse.getMetadata().get(\"name\"))")
    @Mapping(target = "status", expression = "java(cardResponse.getState() == com.backbase.marqeta.clients.model.CardResponse.StateEnum.ACTIVE ? \"Active\" : \"Inactive\")")
    @Mapping(target = "lockStatus", expression = "java(com.backbase.presentation.card.rest.spec.v2.cards.LockStatus.fromValue(cardResponse.getMetadata().get(\"lockStatus\")))")
    @Mapping(target = "replacement", expression = "java(new com.backbase.presentation.card.rest.spec.v2.cards.Replacement().status(cardResponse.getMetadata().get(\"replacementStatus\")).reason(cardResponse.getMetadata().get(\"replacementReason\")))")
    @Mapping(target = "expiryDate", expression = "java(new com.backbase.presentation.card.rest.spec.v2.cards.YearMonth().year(String.valueOf(cardResponse.getExpirationTime().getYear())).month(String.valueOf(cardResponse.getExpirationTime().getMonthValue())))")
    @Mapping(target = "currency", expression = "java(cardResponse.getMetadata().get(\"currency\"))")
    @Mapping(target = "maskedNumber", source = "cardResponse.lastFour")
    @Mapping(target = "delivery", ignore = true)
    @Mapping(target = "limits", expression = "java(cardLimits.getData().stream().map(velocityControlResponse -> {return new com.backbase.presentation.card.rest.spec.v2.cards.CardLimit().id(velocityControlResponse.getToken()).amount(velocityControlResponse.getAmountLimit()).frequency(FrequencyEnum.fromValue(velocityControlResponse.getVelocityWindow().getValue()).getValue()).channel(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase()).minAmount(java.math.BigDecimal.valueOf(Long.parseLong(cardResponse.getMetadata().get(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase().concat(\"MinAmount\")))).setScale(2, java.math.RoundingMode.HALF_UP)).maxAmount(java.math.BigDecimal.valueOf(Long.parseLong(cardResponse.getMetadata().get(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase().concat(\"MaxAmount\")))).setScale(2, java.math.RoundingMode.HALF_UP));}).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "additions", ignore = true)
    CardItem mapCard(CardResponse cardResponse, VelocityControlListResponse cardLimits);

    default String getChannel(Boolean withdrawalsWindow) {
        return withdrawalsWindow != null && withdrawalsWindow ? ATM : ONLINE;
    }

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
    CardUpdateRequest mapUpdateCardRequestForReplacement(String token, RequestReplacementPost requestReplacementPost);

    @Named("replacementMetaData")
    default Map<String, String> getReplacementMetaData(RequestReplacementPost requestReplacementPost) {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put(REPLACEMENT_STATUS, UNDER_REPLACEMENT);
        metaData.put(REPLACEMENT_REASON, requestReplacementPost.getReplacementReason());
        return metaData;
    }

    @Mapping(target = "token", source = "token")
    @Mapping(target = "metadata", source = "token", qualifiedByName = "activationMetaData")
    @Mapping(target = "userToken", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    CardUpdateRequest mapUpdateCardRequestForActivation(String token);

    @Named("activationMetaData")
    default Map<String, String> getActivationMetaData(String token) {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put(REPLACEMENT_STATUS, NOT_UNDER_REPLACEMENT);
        metaData.put(REPLACEMENT_REASON, null);
        return metaData;
    }

    UserCardHolderUpdateModel mapUpdateCardHolderRequest(UserCardHolderResponse response);

    UserCardHolderUpdateModel mapUpdateCardHolderRequest(UserCardHolderResponse response, String id);

    enum FrequencyEnum {
        DAILY("DAILY", "DAY"),

        WEEKLY("WEEKLY", "WEEK"),

        MONTHLY("MONTHLY", "MONTH");

        private String dbsValue;
        private String marqetaValue;

        FrequencyEnum(String dbsValue, String marqetaValue) {
            this.dbsValue = dbsValue;
            this.marqetaValue = marqetaValue;
        }

        public String getValue() {
            return dbsValue;
        }

        public String toString() {
            return String.valueOf(dbsValue);
        }

        public static FrequencyEnum fromValue(String value) {
            for (FrequencyEnum b : FrequencyEnum.values()) {
                if (b.marqetaValue.equals(value)) {
                    return b;
                }
            }
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }
    }

}
