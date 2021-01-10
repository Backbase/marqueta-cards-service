package com.backbase.productled.it;


import static java.util.Collections.singletonList;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.mambu.clients.api.DepositAccountsApi;
import com.backbase.mambu.clients.model.Card;
import com.backbase.marqeta.clients.api.CardsApi;
import com.backbase.marqeta.clients.api.PinsApi;
import com.backbase.marqeta.clients.api.VelocityControlsApi;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionResponse;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.marqeta.clients.model.ControlTokenResponse;
import com.backbase.marqeta.clients.model.VelocityControlResponse;
import com.backbase.presentation.card.rest.spec.v2.cards.ActivatePost;
import com.backbase.presentation.card.rest.spec.v2.cards.ChangeLimitsPostItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatus;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestPinPost;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestReplacementPost;
import com.backbase.presentation.card.rest.spec.v2.cards.ResetPinPost;
import com.backbase.presentation.productsummary.listener.client.v2.productsummary.GetArrangementsByBusinessFunctionQueryParameters;
import com.backbase.presentation.productsummary.listener.client.v2.productsummary.ProductsummaryProductSummaryClient;
import com.backbase.presentation.productsummary.rest.spec.v2.productsummary.ArrangementsByBusinessFunctionGetResponseBody;
import com.backbase.productled.Application;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@SpringBootTest(classes = Application.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
@ActiveProfiles("it")
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class CardsIT {

    static {
        System.setProperty("SIG_SECRET_KEY", "JWTSecretKeyDontUseInProduction!");
    }

    public static final String TEST_JWT =
        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxk"
            + "ZXIiLCJpYXQiOjE0ODQ4MjAxOTYsImV4cCI6MTUxNjM1NjE5NiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJv"
            + "Y2tldEBleGFtcGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2NrZXQiLCJFbWFpbCI6Impyb2Nr"
            + "ZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXSwiaW51aWQiOiJKaW1te"
            + "SJ9.O9TE28ygrHmDjItYK6wRis6wELD5Wtpi6ekeYfR1WqM";

    @MockBean
    private ProductsummaryProductSummaryClient productsummaryProductSummaryClient;

    @MockBean
    private DepositAccountsApi depositAccountsApi;

    @MockBean
    private CardsApi cardsApi;

    @MockBean
    private PinsApi pinsApi;

    @MockBean
    private VelocityControlsApi velocityControlsApi;

    @MockBean
    private com.backbase.marqeta.clients.api.CardTransitionsApi cardTransitionsApi;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Before
    public void setUp() throws IOException {

        when(depositAccountsApi.getAllCards(eq("091000021")))
            .thenReturn(singletonList(new Card().referenceToken("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")));

        when(cardsApi.getCardsToken("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5", null, null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getCardResponse.json"),
                CardResponse.class));

        when(cardsApi.getCardsTokenShowpan("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5", null, true))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/showCvv.json"),
                CardResponse.class));

        when(cardTransitionsApi.postCardtransitions(Mockito.any())).thenReturn(new CardTransitionResponse());

        when(velocityControlsApi
            .getVelocitycontrols("b1e4b06c-06f2-49c8-9c28-016ace3154ad", null, null, null, null, null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getVelocityResponse.json"),
                com.backbase.marqeta.clients.model.VelocityControlListResponse.class));

    }

    @Test
    public void testGetCards() throws Exception {

        // Given
        when(productsummaryProductSummaryClient
            .getArrangementsByBusinessFunction(Mockito.any(GetArrangementsByBusinessFunctionQueryParameters.class)))
            .thenAnswer(invocationOnMock -> ResponseEntity.ok(singletonList(
                new ArrangementsByBusinessFunctionGetResponseBody().withBBAN("091000021"))));

        // When
        ResultActions result = mvc.perform(get("/client-api/v2/cards")
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.*", hasSize(1)))
            .andExpect(jsonPath("$.[0].id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.[0].type", is("Debit")))
            .andExpect(jsonPath("$.[0].subType", is("ATM")))
            .andExpect(jsonPath("$.[0].status", is("Active")))
            .andExpect(jsonPath("$.[0].lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.[0].expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.[0].expiryDate.month", is("12")))
            .andExpect(jsonPath("$.[0].currency", is("USD")))
            .andExpect(jsonPath("$.[0].maskedNumber", is("2053")))
            .andExpect(jsonPath("$.[0].replacement.status", is("NotUnderReplacement")));
    }

    @Test
    public void testGetCardById() throws Exception {

        // When
        ResultActions result = mvc.perform(get("/client-api/v2/cards/{cardId}",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .header("Authorization", TEST_JWT))
            .andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("2053")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")));
    }

    @Test
    public void testLockCard() throws Exception {

        // Given
        when(cardsApi.putCardsToken(eq("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5"), Mockito.any(CardUpdateRequest.class)))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/cardLockedResponse.json"),
                CardResponse.class));

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/lock-status",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(
                objectMapper.writeValueAsString(new LockStatusPost().lockStatus(LockStatus.LOCKED)))
            .contentType("application/json")
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("LOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("2053")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")));
    }

    @Test
    public void testUnLockCard() throws Exception {

        // Given
        when(cardsApi.putCardsToken(eq("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5"), Mockito.any(CardUpdateRequest.class)))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/cardUnlockedResponse.json"),
                CardResponse.class));

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/lock-status",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(
                objectMapper.writeValueAsString(new LockStatusPost().lockStatus(LockStatus.UNLOCKED)))
            .contentType("application/json")
            .header("Authorization", TEST_JWT))
            .andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("2053")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")));
    }

    @Test
    public void testRequestReplacement() throws Exception {

        // Given
        when(cardsApi.putCardsToken(eq("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5"), Mockito.any(CardUpdateRequest.class)))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/replacementCardResponse.json"),
                CardResponse.class));

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/replacement",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(objectMapper.writeValueAsString(new RequestReplacementPost().replacementReason("stolen")))
            .contentType("application/json")
            .header("Authorization", TEST_JWT))
            .andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.status", is("Inactive")))
            .andExpect(jsonPath("$.lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("2053")))
            .andExpect(jsonPath("$.replacement.status", is("UnderReplacement")));
    }

    @Test
    public void testActivation() throws Exception {

        // Given
        when(cardsApi.putCardsToken(eq("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5"), Mockito.any(CardUpdateRequest.class)))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/cardUnlockedResponse.json"),
                CardResponse.class));

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/activation",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(objectMapper.writeValueAsString(new ActivatePost().token("132")))
            .contentType("application/json")
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("2053")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")));
    }

    @Test
    public void testChangeLimits() throws Exception {

        // Given
        when(velocityControlsApi.putVelocitycontrolsToken(eq("acdca953-f539-412f-9c03-e49f3c7f7b5e"), Mockito.any()))
            .thenReturn(new VelocityControlResponse());

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/limits",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(objectMapper
                .writeValueAsString(singletonList(new ChangeLimitsPostItem().id("d5f5e333-9463-4050-b554-9d0d1119d64e")
                    .amount(BigDecimal.valueOf(5000)))))
            .contentType("application/json")
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("2053")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")))
            .andExpect(jsonPath("$.limits[0].id", is("d5f5e333-9463-4050-b554-9d0d1119d64e")))
            .andExpect(jsonPath("$.limits[0].amount", is("5000.00")));
    }

    @Test
    public void testResetPin() throws Exception {

        // Given
        when(pinsApi.postPinsControltoken(Mockito.any()))
            .thenReturn(new ControlTokenResponse().controlToken("test"));

        Mockito.doNothing().when(pinsApi).putPins(Mockito.any());

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/pin/reset",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(objectMapper
                .writeValueAsString(new ResetPinPost().token("132").pin("7278")))
            .contentType("application/json")
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("2053")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")))
            .andExpect(jsonPath("$.limits[0].id", is("d5f5e333-9463-4050-b554-9d0d1119d64e")))
            .andExpect(jsonPath("$.limits[0].amount", is("5000.00")));
    }

    @Test
    public void testResetPinWhenWrongCvv() throws Exception {

        // Given
        when(pinsApi.postPinsControltoken(Mockito.any()))
            .thenReturn(new ControlTokenResponse().controlToken("test"));

        Mockito.doNothing().when(pinsApi).putPins(Mockito.any());

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/pin/reset",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(objectMapper
                .writeValueAsString(new ResetPinPost().token("112").pin("7278")))
            .contentType("application/json")
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then
        result.andExpect(status().isBadRequest());
    }

    @Test
    public void testRequestPin() throws Exception {

        // When
        ResultActions result = mvc.perform(post("/client-api/v2/cards/{id}/pin/request",
            "aeeff27f-94a3-4687-9fd6-1f94cf26b2e5")
            .content(objectMapper
                .writeValueAsString(new RequestPinPost().token("112")))
            .contentType("application/json")
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then
        result.andExpect(status().isOk());
    }
}