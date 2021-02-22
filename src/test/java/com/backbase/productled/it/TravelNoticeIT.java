package com.backbase.productled.it;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.backbase.dbs.user.manager.api.service.v2.UserManagementApi;
import com.backbase.dbs.user.manager.api.service.v2.model.GetUser;
import com.backbase.marqeta.clients.api.CardsApi;
import com.backbase.marqeta.clients.api.UsersApi;
import com.backbase.marqeta.clients.model.CardHolderModel;
import com.backbase.marqeta.clients.model.UserCardHolderResponse;
import com.backbase.presentation.card.rest.spec.v2.cards.Destination;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.Application;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
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
import org.springframework.http.MediaType;
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
public class TravelNoticeIT {

    static {
        System.setProperty("SIG_SECRET_KEY", "JWTSecretKeyDontUseInProduction!");
    }

    public static final String TEST_JWT =
        "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJpc3MiOiJPbmxpbmUgSldUIEJ1aWxk"
            + "ZXIiLCJpYXQiOjE0ODQ4MjAxOTYsImV4cCI6MTUxNjM1NjE5NiwiYXVkIjoid3d3LmV4YW1wbGUuY29tIiwic3ViIjoianJv"
            + "Y2tldEBleGFtcGxlLmNvbSIsIkdpdmVuTmFtZSI6IkpvaG5ueSIsIlN1cm5hbWUiOiJSb2NrZXQiLCJFbWFpbCI6Impyb2Nr"
            + "ZXRAZXhhbXBsZS5jb20iLCJSb2xlIjpbIk1hbmFnZXIiLCJQcm9qZWN0IEFkbWluaXN0cmF0b3IiXSwiaW51aWQiOiJKaW1te"
            + "SJ9.O9TE28ygrHmDjItYK6wRis6wELD5Wtpi6ekeYfR1WqM";

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsersApi usersApi;

    @MockBean
    private CardsApi cardsApi;

    @MockBean
    private UserManagementApi userManagementApi;

    @Before
    public void setUp() throws IOException {

        when(userManagementApi.getUserById(Mockito.any(), Mockito.any()))
            .thenReturn(new GetUser().externalId("1be8bb0b-dcdd-4219-81ab-565621d3707c"));

        when(cardsApi.getCardsToken("aeeff27f-94a3-4687-9fd6-1f94cf26b2e5", null, null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getCardResponse.json"),
                com.backbase.marqeta.clients.model.CardResponse.class));
    }

    @Test
    public void testGetTravelNotices() throws Exception {

        // Given
        Mockito.when(usersApi.getUsersToken("1be8bb0b-dcdd-4219-81ab-565621d3707c", null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getUser.json"),
                UserCardHolderResponse.class));

        MockHttpServletRequestBuilder requestBuilder = get("/client-api/v2/travel-notices")
            .header("Authorization", TEST_JWT);

        // When
        ResultActions result = mvc.perform(requestBuilder).andDo(print());

        // Then the request is successful
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.*", hasSize(1)))
            .andExpect(jsonPath("$.[0].id", is("a1ae90c1-93d8-4257-8bf5-cb56818a2537")))
            .andExpect(jsonPath("$.[0].departureDate", is("2021-01-29")))
            .andExpect(jsonPath("$.[0].arrivalDate", is("2021-01-31")))
            .andExpect(jsonPath("$.[0].cardIds[0]", is("4694a2c0-8838-4f1b-9c3b-1bb1ea8eb829")));
    }

    @Test
    public void testGetTravelNoticeById() throws Exception {

        // Given
        Mockito.when(usersApi.getUsersToken("1be8bb0b-dcdd-4219-81ab-565621d3707c", null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getUser.json"),
                UserCardHolderResponse.class));

        MockHttpServletRequestBuilder requestBuilder = get("/client-api/v2/travel-notices/{id}",
            "a1ae90c1-93d8-4257-8bf5-cb56818a2537")
            .header("Authorization", TEST_JWT);

        // When
        ResultActions result = mvc.perform(requestBuilder).andDo(print());

        // Then the request is successful
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.id", is("a1ae90c1-93d8-4257-8bf5-cb56818a2537")))
            .andExpect(jsonPath("$.departureDate", is("2021-01-29")))
            .andExpect(jsonPath("$.arrivalDate", is("2021-01-31")))
            .andExpect(jsonPath("$.cardIds[0]", is("4694a2c0-8838-4f1b-9c3b-1bb1ea8eb829")));
    }

    @Test
    public void testDeleteTravelNoticeById() throws Exception {

        // Given
        Mockito.when(usersApi.getUsersToken("1be8bb0b-dcdd-4219-81ab-565621d3707c", null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getUser.json"),
                UserCardHolderResponse.class));

        Mockito.when(usersApi.putUsersToken(eq("1be8bb0b-dcdd-4219-81ab-565621d3707c"), Mockito.any()))
            .thenReturn(new CardHolderModel());

        MockHttpServletRequestBuilder requestBuilder = delete("/client-api/v2/travel-notices/{id}",
            "a1ae90c1-93d8-4257-8bf5-cb56818a2537")
            .header("Authorization", TEST_JWT);

        // When
        ResultActions result = mvc.perform(requestBuilder).andDo(print());

        // Then the request is successful
        result.andExpect(status().is2xxSuccessful());
    }


    @Test
    public void testCreateTravelNotice() throws Exception {

        // Given
        Mockito.when(usersApi.getUsersToken("1be8bb0b-dcdd-4219-81ab-565621d3707c", null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getUser.json"),
                UserCardHolderResponse.class));

        Mockito.when(usersApi.putUsersToken(eq("1be8bb0b-dcdd-4219-81ab-565621d3707c"), Mockito.any()))
            .thenReturn(new CardHolderModel());

        MockHttpServletRequestBuilder requestBuilder = post("/client-api/v2/travel-notices",
            "a1ae90c1-93d8-4257-8bf5-cb56818a2537")
            .content(objectMapper.writeValueAsString(
                new TravelNotice().arrivalDate("2021-01-31")
                    .departureDate("2021-01-29").addDestinationsItem(new Destination().country("IND"))
                    .addCardIdsItem("4694a2c0-8838-4f1b-9c3b-1bb1ea8eb829")))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", TEST_JWT);

        // When
        ResultActions result = mvc.perform(requestBuilder).andDo(print());

        // Then the request is successful
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.departureDate", is("2021-01-29")))
            .andExpect(jsonPath("$.arrivalDate", is("2021-01-31")))
            .andExpect(jsonPath("$.cardIds[0]", is("4694a2c0-8838-4f1b-9c3b-1bb1ea8eb829")));

        // When
        ObjectMapper om = Mockito.spy(new ObjectMapper());
        Mockito.when(om.writeValueAsString(Mockito.any())).thenThrow(new JsonProcessingException("") {
        });

        // then
        mvc.perform(post("/client-api/v2/travel-notices",
            "a1ae90c1-93d8-4257-8bf5-cb56818a2537")
            .content(objectMapper.writeValueAsString(
                new TravelNotice().arrivalDate("2021-01-31")
                    .departureDate("2021-01-29").addDestinationsItem(new Destination().country("IND"))
                    .addCardIdsItem("4694a2c0-8838-4f1b-9c3b-1bb1ea8eb829")))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", TEST_JWT)).andDo(print());


    }


    @Test
    public void testUpdateTravelNotice() throws Exception {

        // Given
        Mockito.when(usersApi.getUsersToken("1be8bb0b-dcdd-4219-81ab-565621d3707c", null))
            .thenReturn(objectMapper.readValue(new File("src/test/resources/response/getUser.json"),
                UserCardHolderResponse.class));

        Mockito.when(usersApi.putUsersToken(eq("1be8bb0b-dcdd-4219-81ab-565621d3707c"), Mockito.any()))
            .thenReturn(new CardHolderModel());

        // When
        ResultActions result = mvc.perform(put("/client-api/v2/travel-notices/{id}",
            "a1ae90c1-93d8-4257-8bf5-cb56818a2537")
            .content(objectMapper.writeValueAsString(
                new TravelNotice().arrivalDate("2021-01-31")
                    .departureDate("2021-01-29").addDestinationsItem(new Destination().country("IND"))
                    .addCardIdsItem("4694a2c0-8838-4f1b-9c3b-1bb1ea8eb829")))
            .contentType(MediaType.APPLICATION_JSON)
            .header("Authorization", TEST_JWT)).andDo(print());

        // Then the request is successful
        result.andExpect(status().isOk())
            .andExpect(jsonPath("$.departureDate", is("2021-01-29")))
            .andExpect(jsonPath("$.arrivalDate", is("2021-01-31")))
            .andExpect(jsonPath("$.cardIds[0]", is("4694a2c0-8838-4f1b-9c3b-1bb1ea8eb829")));
    }

}
