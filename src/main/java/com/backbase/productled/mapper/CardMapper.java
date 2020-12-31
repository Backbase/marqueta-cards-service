package com.backbase.productled.mapper;

import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardResponse.StateEnum;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardTransitionRequest.ChannelEnum;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.presentation.card.rest.spec.v2.cards.CardHolder;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatus;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.Replacement;
import com.backbase.presentation.card.rest.spec.v2.cards.YearMonth;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class CardMapper {

    private static final String BRAND = "brand";
    private static final String TYPE = "type";
    private static final String SUB_TYPE = "subType";
    private static final String CARD_HOLDER_NAME = "cardHolderName";
    private static final String NAME = "name";
    private static final String ACTIVE = "Active";
    private static final String INACTIVE = "Inactive";
    private static final String REPLACEMENT_STATUS = "replacementStatus";
    private static final String CURRENCY = "currency";
    private static final String LOCK_STATUS = "lockStatus";

    public CardItem mapCard(CardResponse cardResponse) {

        return new CardItem()
            .id(cardResponse.getToken())
            .brand(Objects.requireNonNull(cardResponse.getMetadata()).get(BRAND))
            .type(cardResponse.getMetadata().get(TYPE))
            .subType(cardResponse.getMetadata().get(SUB_TYPE))
            .holder(new CardHolder().name(cardResponse.getMetadata().get(CARD_HOLDER_NAME)))
            .name(cardResponse.getMetadata().get(NAME))
            .status(cardResponse.getState() == StateEnum.ACTIVE ? ACTIVE : INACTIVE)
            .lockStatus(LockStatus.fromValue(cardResponse.getMetadata().get(LOCK_STATUS)))
            .replacement(new Replacement().status(cardResponse.getMetadata().get(REPLACEMENT_STATUS)))
            .expiryDate(new YearMonth().year(String.valueOf(cardResponse.getExpirationTime().getYear()))
                .month(String.valueOf(cardResponse.getExpirationTime().getMonthValue())))
            .currency(cardResponse.getMetadata().get(CURRENCY))
            .maskedNumber(cardResponse.getLastFour());

    }

    public CardTransitionRequest mapCardTransitionRequest(String id) {
        return new CardTransitionRequest()
            .token(UUID.randomUUID().toString())
            .cardToken(id)
            .channel(ChannelEnum.API)
            .state(CardTransitionRequest.StateEnum.ACTIVE);
    }

    public CardUpdateRequest mapCardLockStatusRequest(String token, LockStatusPost lockStatusPost) {
        return new CardUpdateRequest().token(token)
            .metadata(Collections.singletonMap(LOCK_STATUS, lockStatusPost.getLockStatus().toString()));
    }
}
