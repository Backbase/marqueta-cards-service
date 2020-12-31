package com.backbase.productled;


import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.dbs.arrangement.api.service.v2.ProductSummaryApi;
import com.backbase.dbs.arrangement.api.service.v2.model.ProductSummaryItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatus;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Collections;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

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
    private ProductSummaryApi productSummaryApi;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetCards() throws Exception {

        Mockito.when(productSummaryApi
            .getArrangementsByBusinessFunction(Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any()))
            .thenAnswer(invocationOnMock -> Collections
                .singletonList(
                    new ProductSummaryItem().BBAN("091000021")));

        MockHttpServletRequestBuilder requestBuilder = get("/client-api/v2/cards")
            .header("Authorization", TEST_JWT);

        ResultActions result = mvc.perform(requestBuilder).andDo(print());

        // Then the request is successful
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.*", hasSize(1)))
            .andExpect(jsonPath("$.[0].id", is("5bf15f0d-36f5-40f2-8cf1-931bd5a8d7d9")))
            .andExpect(jsonPath("$.[0].type", is("Debit")))
            .andExpect(jsonPath("$.[0].subType", is("ATM")))
            .andExpect(jsonPath("$.[0].name", is("Blue Card")))
            .andExpect(jsonPath("$.[0].status", is("Active")))
            .andExpect(jsonPath("$.[0].lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.[0].expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.[0].expiryDate.month", is("12")))
            .andExpect(jsonPath("$.[0].currency", is("USD")))
            .andExpect(jsonPath("$.[0].maskedNumber", is("8155")))
            .andExpect(jsonPath("$.[0].replacement.status", is("NotUnderReplacement")));
    }

    @Test
    public void testLockCard() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = post("/client-api/v2/cards/{id}/lock-status",
            "5bf15f0d-36f5-40f2-8cf1-931bd5a8d7d9")
            .content(
                objectMapper.writeValueAsString(new LockStatusPost().lockStatus(LockStatus.LOCKED)))
            .contentType("application/json")
            .header("Authorization", TEST_JWT);

        ResultActions result = mvc.perform(requestBuilder).andDo(print());

        // Then the request is successful
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("5bf15f0d-36f5-40f2-8cf1-931bd5a8d7d9")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.name", is("Blue Card")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("LOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("8155")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")));
    }

    @Test
    public void testUnLockCard() throws Exception {

        MockHttpServletRequestBuilder requestBuilder = post("/client-api/v2/cards/{id}/lock-status",
            "5bf15f0d-36f5-40f2-8cf1-931bd5a8d7d9")
            .content(
                objectMapper.writeValueAsString(new LockStatusPost().lockStatus(LockStatus.UNLOCKED)))
            .contentType("application/json")
            .header("Authorization", TEST_JWT);

        ResultActions result = mvc.perform(requestBuilder).andDo(print());

        // Then the request is successful
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("5bf15f0d-36f5-40f2-8cf1-931bd5a8d7d9")))
            .andExpect(jsonPath("$.type", is("Debit")))
            .andExpect(jsonPath("$.subType", is("ATM")))
            .andExpect(jsonPath("$.name", is("Blue Card")))
            .andExpect(jsonPath("$.status", is("Active")))
            .andExpect(jsonPath("$.lockStatus", is("UNLOCKED")))
            .andExpect(jsonPath("$.expiryDate.year", is("2024")))
            .andExpect(jsonPath("$.expiryDate.month", is("12")))
            .andExpect(jsonPath("$.currency", is("USD")))
            .andExpect(jsonPath("$.maskedNumber", is("8155")))
            .andExpect(jsonPath("$.replacement.status", is("NotUnderReplacement")));
    }
}
