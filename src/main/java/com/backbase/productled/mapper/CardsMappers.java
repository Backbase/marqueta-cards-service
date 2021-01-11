package com.backbase.productled.mapper;

import static com.backbase.productled.utils.CardConstants.ACTIVE;
import static com.backbase.productled.utils.CardConstants.ATM;
import static com.backbase.productled.utils.CardConstants.CARD_HOLDER_NAME;
import static com.backbase.productled.utils.CardConstants.DELIVERED;
import static com.backbase.productled.utils.CardConstants.INACTIVE;
import static com.backbase.productled.utils.CardConstants.IN_TRANSIT;
import static com.backbase.productled.utils.CardConstants.LOCK_STATUS;
import static com.backbase.productled.utils.CardConstants.NOT_UNDER_REPLACEMENT;
import static com.backbase.productled.utils.CardConstants.ONLINE;
import static com.backbase.productled.utils.CardConstants.ORDERED;
import static com.backbase.productled.utils.CardConstants.PROCESSED;
import static com.backbase.productled.utils.CardConstants.REPLACEMENT_REASON;
import static com.backbase.productled.utils.CardConstants.REPLACEMENT_STATUS;
import static com.backbase.productled.utils.CardConstants.UNDER_REPLACEMENT;

import com.backbase.marqeta.clients.model.CardRequest;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardResponse.StateEnum;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.marqeta.clients.model.VelocityControlListResponse;
import com.backbase.presentation.card.rest.spec.v2.cards.CardHolder;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.Delivery;
import com.backbase.presentation.card.rest.spec.v2.cards.DeliveryTransitStep;
import com.backbase.presentation.card.rest.spec.v2.cards.DeliveryTransitStep.StatusEnum;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatus;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.Replacement;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestReplacementPost;
import com.backbase.presentation.card.rest.spec.v2.cards.YearMonth;
import java.time.OffsetDateTime;
import java.util.Arrays;
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
    @Mapping(target = "holder", source = "cardResponse", qualifiedByName = "getCardHolder")
    @Mapping(target = "name", expression = "java(cardResponse.getMetadata().get(\"name\"))")
    @Mapping(target = "status", source = "cardResponse", qualifiedByName = "getStatus")
    @Mapping(target = "lockStatus", source = "cardResponse", qualifiedByName = "getLockStatus")
    @Mapping(target = "replacement", source = "cardResponse", qualifiedByName = "getReplacement")
    @Mapping(target = "expiryDate", source = "cardResponse", qualifiedByName = "getExpiryDate")
    @Mapping(target = "currency", expression = "java(cardResponse.getMetadata().get(\"currency\"))")
    @Mapping(target = "limits", expression = "java(cardLimits.getData().stream().map(velocityControlResponse -> {return new com.backbase.presentation.card.rest.spec.v2.cards.CardLimit().id(velocityControlResponse.getToken()).amount(velocityControlResponse.getAmountLimit()).frequency(com.backbase.productled.utils.FrequencyEnum.fromValue(velocityControlResponse.getVelocityWindow().getValue()).getValue()).channel(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase()).minAmount(java.math.BigDecimal.valueOf(Long.parseLong(cardResponse.getMetadata().get(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase().concat(\"MinAmount\")))).setScale(2, java.math.RoundingMode.HALF_UP)).maxAmount(java.math.BigDecimal.valueOf(Long.parseLong(cardResponse.getMetadata().get(getChannel(velocityControlResponse.getIncludeWithdrawals()).toLowerCase().concat(\"MaxAmount\")))).setScale(2, java.math.RoundingMode.HALF_UP));}).collect(java.util.stream.Collectors.toList()))")
    @Mapping(target = "delivery", source = "cardResponse", qualifiedByName = "getDelivery")
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
    CardTransitionRequest mapCardTransitionRequest(String id,
        com.backbase.marqeta.clients.model.CardTransitionRequest.StateEnum state);

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

    @Named("getStatus")
    default String getStatus(CardResponse cardResponse) {
        if (Arrays.asList(StateEnum.ACTIVE, StateEnum.SUSPENDED).contains(cardResponse.getState())) {
            return ACTIVE;
        }
        return INACTIVE;
    }

    @Named("getLockStatus")
    default LockStatus getLockStatus(CardResponse cardResponse) {
        if (com.backbase.marqeta.clients.model.CardResponse.StateEnum.ACTIVE == cardResponse.getState()) {
            return LockStatus.UNLOCKED;
        }
        return LockStatus.LOCKED;
    }

    @Named("getReplacement")
    default Replacement getReplacement(CardResponse cardResponse) {
        if (cardResponse.getMetadata() != null) {
            return new Replacement()
                .status(cardResponse.getMetadata().get(REPLACEMENT_STATUS))
                .reason(cardResponse.getMetadata().get(REPLACEMENT_REASON));
        }
        return new Replacement().status(NOT_UNDER_REPLACEMENT);
    }

    @Named("getExpiryDate")
    default YearMonth getExpiryDate(CardResponse cardResponse) {
        return new YearMonth()
            .year(String.valueOf(cardResponse.getExpirationTime().getYear()))
            .month(String.valueOf(cardResponse.getExpirationTime().getMonthValue()));

    }

    @Named("getCardHolder")
    default CardHolder getCardHolder(CardResponse cardResponse) {
        if (cardResponse.getMetadata() == null) {
            return null;
        }
        return new CardHolder()
            .name(cardResponse.getMetadata().get(CARD_HOLDER_NAME));
    }

    @Named("getDelivery")
    default Delivery getDelivery(CardResponse cardResponse) {
        if (cardResponse.getState() == StateEnum.UNACTIVATED) {
            return new Delivery()
                .transitSteps(Arrays.asList(
                    new DeliveryTransitStep().name(ORDERED).status(StatusEnum.SUCCESS)
                        .stepDateTime(OffsetDateTime.now().minusMinutes(30)),
                    new DeliveryTransitStep().name(PROCESSED).status(StatusEnum.SUCCESS)
                        .stepDateTime(OffsetDateTime.now().minusMinutes(25)),
                    new DeliveryTransitStep().name(IN_TRANSIT).status(StatusEnum.PENDING),
                    new DeliveryTransitStep().name(DELIVERED).status(StatusEnum.PENDING)
                ));
        }
        return null;
    }
}
