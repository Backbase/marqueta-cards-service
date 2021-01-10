package com.backbase.productled.mapper;

import static com.backbase.productled.utils.CardConstants.ATM;
import static com.backbase.productled.utils.CardConstants.LOCK_STATUS;
import static com.backbase.productled.utils.CardConstants.NOT_UNDER_REPLACEMENT;
import static com.backbase.productled.utils.CardConstants.ONLINE;
import static com.backbase.productled.utils.CardConstants.REPLACEMENT_REASON;
import static com.backbase.productled.utils.CardConstants.REPLACEMENT_STATUS;
import static com.backbase.productled.utils.CardConstants.UNDER_REPLACEMENT;

import com.backbase.marqeta.clients.model.CardRequest;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.marqeta.clients.model.VelocityControlListResponse;
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


    @Mapping(target = "id", source = "cardResponse.token")
    @Mapping(target = "maskedNumber", source = "cardResponse.lastFour")
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
    @Mapping(target = "limits", expression = "java(cardLimits.getData().stream().map(velocityControlResponse -> {return new com.backbase.presentation.card.rest.spec.v2.cards.CardLimit().id(velocityControlResponse.getToken()).amount(velocityControlResponse.getAmountLimit()).frequency(com.backbase.productled.utils.FrequencyEnum.fromValue(velocityControlResponse.getVelocityWindow().getValue()).getValue()).channel(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase()).minAmount(java.math.BigDecimal.valueOf(Long.parseLong(cardResponse.getMetadata().get(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase().concat(\"MinAmount\")))).setScale(2, java.math.RoundingMode.HALF_UP)).maxAmount(java.math.BigDecimal.valueOf(Long.parseLong(cardResponse.getMetadata().get(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase().concat(\"MaxAmount\")))).setScale(2, java.math.RoundingMode.HALF_UP));}).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "delivery", ignore = true)
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

    @Mapping(target = "token", source = "token")
    @Mapping(target = "metadata", source = "requestReplacementPost", qualifiedByName = "replacementMetaData")
    @Mapping(target = "userToken", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    CardUpdateRequest mapUpdateCardRequestForReplacement(String token, RequestReplacementPost requestReplacementPost);

    @Mapping(target = "token", source = "token")
    @Mapping(target = "metadata", source = "token", qualifiedByName = "activationMetaData")
    @Mapping(target = "userToken", ignore = true)
    @Mapping(target = "fulfillment", ignore = true)
    CardUpdateRequest mapUpdateCardRequestForActivation(String token);

    UserCardHolderUpdateModel mapUpdateCardHolderRequest(UserCardHolderResponse response);

    default CardRequest mapCreateCardRequest(CardResponse cardDetails) {
        return new CardRequest()
            .cardProductToken(cardDetails.getCardProductToken())
            .userToken(cardDetails.getUserToken())
            .metadata(cardDetails.getMetadata());
    }

    @Named("activationMetaData")
    default Map<String, String> getActivationMetaData(String token) {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put(REPLACEMENT_STATUS, NOT_UNDER_REPLACEMENT);
        metaData.put(REPLACEMENT_REASON, null);
        return metaData;
    }

    @Named("replacementMetaData")
    default Map<String, String> getReplacementMetaData(RequestReplacementPost requestReplacementPost) {
        HashMap<String, String> metaData = new HashMap<>();
        metaData.put(REPLACEMENT_STATUS, UNDER_REPLACEMENT);
        metaData.put(REPLACEMENT_REASON, requestReplacementPost.getReplacementReason());
        return metaData;
    }

    @Named("statusMetadata")
    default Map<String, String> getStatusMetadata(LockStatusPost lockStatusPost) {
        return Collections.singletonMap(LOCK_STATUS, lockStatusPost.getLockStatus().toString());
    }
}
