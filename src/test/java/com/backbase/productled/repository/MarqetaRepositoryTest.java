package com.backbase.productled.repository;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.backbase.buildingblocks.presentation.errors.BadRequestException;
import com.backbase.buildingblocks.presentation.errors.InternalServerErrorException;
import com.backbase.buildingblocks.presentation.errors.NotFoundException;
import com.backbase.marqeta.clients.api.CardTransitionsApi;
import com.backbase.marqeta.clients.api.CardsApi;
import com.backbase.marqeta.clients.api.PinsApi;
import com.backbase.marqeta.clients.api.UsersApi;
import com.backbase.marqeta.clients.api.VelocityControlsApi;
import com.backbase.marqeta.clients.model.CardListResponse;
import com.backbase.marqeta.clients.model.CardRequest;
import com.backbase.marqeta.clients.model.CardResponse;
import com.backbase.marqeta.clients.model.CardTransitionRequest;
import com.backbase.marqeta.clients.model.CardUpdateRequest;
import com.backbase.marqeta.clients.model.ControlTokenRequest;
import com.backbase.marqeta.clients.model.ControlTokenResponse;
import com.backbase.marqeta.clients.model.PinRequest;
import com.backbase.marqeta.clients.model.UserCardHolderUpdateModel;
import com.backbase.marqeta.clients.model.VelocityControlUpdateRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

@RunWith(MockitoJUnitRunner.class)
public class MarqetaRepositoryTest {

    private static final String CARD_TOKEN = "aeeff27f-94a3-4687-9fd6-1f94cf26b2e";
    private static final String USER_TOKEN = "1be8bb0b-dcdd-4219-81ab-565621d3707c";
    private static final String PIN_CONTROL_TOKEN = "ace86f53-f7fd-4113-8ea7-e4f2db59f819";
    private static final String CARD_PRODUCT_TOKEN = "b1e4b06c-06f2-49c8-9c28-016ace3154ad";
    private static final String CVV = "123";

    @Mock
    private CardsApi cardsApi;

    @Mock
    private CardTransitionsApi cardTransitionsApi;

    @Mock
    private PinsApi pinsApi;

    @Mock
    private VelocityControlsApi velocityControlsApi;

    @Mock
    private UsersApi usersApi;

    @InjectMocks
    private MarqetaRepository marqetaRepository;

    @Test
    public void testGetCardDetails() {

        // given
        Mockito.when(cardsApi.getCardsToken(CARD_TOKEN, null, null)).thenReturn(new CardResponse().token(CARD_TOKEN));

        // when
        CardResponse response = marqetaRepository.getCardDetails(CARD_TOKEN);

        // then
        Assert.assertEquals(CARD_TOKEN, response.getToken());
    }

    @Test(expected = NotFoundException.class)
    public void testGetCardDetailsWhenCardNotFound() {

        // given
        Mockito.when(cardsApi.getCardsToken(CARD_TOKEN, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.getCardDetails(CARD_TOKEN);
    }

    @Test(expected = BadRequestException.class)
    public void testGetCardDetailsWhenBadRequestException() {

        // given
        Mockito.when(cardsApi.getCardsToken(CARD_TOKEN, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.getCardDetails(CARD_TOKEN);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetCardDetailsWhenInternalServerErrorException() {

        // given
        Mockito.when(cardsApi.getCardsToken(CARD_TOKEN, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.getCardDetails(CARD_TOKEN);
    }

    @Test
    public void testGetUserCards() {

        // given
        Mockito.when(cardsApi.getCardsUserToken(USER_TOKEN, null, null, null, null))
            .thenReturn(new CardListResponse().count(1)
                .addDataItem(new CardResponse().token(CARD_TOKEN).userToken(USER_TOKEN)));

        // when
        CardListResponse response = marqetaRepository.getUserCards(USER_TOKEN);

        // then
        Assert.assertNotNull(response.getCount());
        Assert.assertNotNull(response.getData());
        Assert.assertNotNull(response.getData().get(0));
        Assert.assertEquals(1, response.getCount().intValue());
        Assert.assertEquals(CARD_TOKEN, response.getData().get(0).getToken());
        Assert.assertEquals(USER_TOKEN, response.getData().get(0).getUserToken());
    }

    @Test(expected = NotFoundException.class)
    public void testGetUserCardsWhenNotFoundException() {

        // given
        Mockito.when(cardsApi.getCardsUserToken(USER_TOKEN, null, null, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.getUserCards(USER_TOKEN);
    }

    @Test(expected = BadRequestException.class)
    public void testGetUserCardsWhenBadRequestException() {

        // given
        Mockito.when(cardsApi.getCardsUserToken(USER_TOKEN, null, null, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.getUserCards(USER_TOKEN);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetUserCardsWhenInternalServerErrorException() {

        // given
        Mockito.when(cardsApi.getCardsUserToken(USER_TOKEN, null, null, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.getUserCards(USER_TOKEN);
    }

    @Test
    public void testCreateCard() {

        // given
        CardRequest cardRequest = new CardRequest();
        Mockito.when(cardsApi.postCards(false, false, cardRequest))
            .thenReturn(new CardResponse().token(CARD_TOKEN).userToken(USER_TOKEN));

        // when
        CardResponse response = marqetaRepository.createCard(cardRequest);

        // then
        Assert.assertEquals(CARD_TOKEN, response.getToken());
        Assert.assertEquals(USER_TOKEN, response.getUserToken());
    }

    @Test(expected = NotFoundException.class)
    public void testCreateCardWhenNotFoundException() {

        // given
        CardRequest cardRequest = new CardRequest();
        Mockito.when(cardsApi.postCards(false, false, cardRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.createCard(cardRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testCreateCardWhenBadRequestException() {

        // given
        CardRequest cardRequest = new CardRequest();
        Mockito.when(cardsApi.postCards(false, false, cardRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.createCard(cardRequest);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testCreateCardWhenInternalServerErrorException() {

        // given
        CardRequest cardRequest = new CardRequest();
        Mockito.when(cardsApi.postCards(false, false, cardRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.createCard(cardRequest);
    }

    @Test
    public void testUpdateCard() {

        // given
        CardUpdateRequest cardUpdateRequest = new CardUpdateRequest();
        when(cardsApi.putCardsToken(CARD_TOKEN, cardUpdateRequest))
            .thenReturn(new CardResponse().token(CARD_TOKEN).userToken(USER_TOKEN));

        // when
        CardResponse response = marqetaRepository.updateCard(CARD_TOKEN, cardUpdateRequest);

        // then
        Assert.assertEquals(CARD_TOKEN, response.getToken());
        Assert.assertEquals(USER_TOKEN, response.getUserToken());
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateCardWhenNotFoundException() {

        // given
        CardUpdateRequest cardUpdateRequest = new CardUpdateRequest();
        when(cardsApi.putCardsToken(CARD_TOKEN, cardUpdateRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.updateCard(CARD_TOKEN, cardUpdateRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdateCardWhenBadRequestException() {

        // given
        CardUpdateRequest cardUpdateRequest = new CardUpdateRequest();
        when(cardsApi.putCardsToken(CARD_TOKEN, cardUpdateRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.updateCard(CARD_TOKEN, cardUpdateRequest);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testUpdateCardWhenInternalServerErrorException() {

        // given
        CardUpdateRequest cardUpdateRequest = new CardUpdateRequest();
        when(cardsApi.putCardsToken(CARD_TOKEN, cardUpdateRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.updateCard(CARD_TOKEN, cardUpdateRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testPostCardTransitionsWhenNotFoundException() {

        // given
        CardTransitionRequest cardTransitionRequest = new CardTransitionRequest();
        when(cardTransitionsApi.postCardtransitions(cardTransitionRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.postCardTransitions(cardTransitionRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testPostCardTransitionsWhenBadRequestException() {

        // given
        CardTransitionRequest cardTransitionRequest = new CardTransitionRequest();
        when(cardTransitionsApi.postCardtransitions(cardTransitionRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.postCardTransitions(cardTransitionRequest);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testPostCardTransitionsWhenInternalServerErrorException() {

        // given
        CardTransitionRequest cardTransitionRequest = new CardTransitionRequest();
        when(cardTransitionsApi.postCardtransitions(cardTransitionRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.postCardTransitions(cardTransitionRequest);
    }

    @Test
    public void testGetCardCvv() {

        // given
        when(cardsApi.getCardsTokenShowpan(CARD_TOKEN, null, true))
            .thenReturn(new CardResponse().token(CARD_TOKEN).userToken(USER_TOKEN).cvvNumber(CVV));

        // when
        CardResponse response = marqetaRepository.getCardCvv(CARD_TOKEN);

        // then
        Assert.assertEquals(CVV, response.getCvvNumber());
    }

    @Test(expected = NotFoundException.class)
    public void testGetCardCvvWhenNotFoundException() {

        // given
        when(cardsApi.getCardsTokenShowpan(CARD_TOKEN, null, true))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.getCardCvv(CARD_TOKEN);
    }

    @Test(expected = BadRequestException.class)
    public void testGetCardCvvWhenBadRequestException() {

        // given
        when(cardsApi.getCardsTokenShowpan(CARD_TOKEN, null, true))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.getCardCvv(CARD_TOKEN);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetCardCvvWhenInternalServerErrorException() {

        // given
        when(cardsApi.getCardsTokenShowpan(CARD_TOKEN, null, true))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.getCardCvv(CARD_TOKEN);
    }

    @Test
    public void testGetPinControlToken() {

        // given
        ControlTokenRequest controlTokenRequest = new ControlTokenRequest();
        when(pinsApi.postPinsControltoken(controlTokenRequest))
            .thenReturn(new ControlTokenResponse().controlToken(PIN_CONTROL_TOKEN));

        // when
        ControlTokenResponse response = marqetaRepository.getPinControlToken(controlTokenRequest);

        // then
        Assert.assertEquals(PIN_CONTROL_TOKEN, response.getControlToken());

    }

    @Test(expected = NotFoundException.class)
    public void testGetPinControlTokenWhen() {

        // given
        ControlTokenRequest controlTokenRequest = new ControlTokenRequest();
        when(pinsApi.postPinsControltoken(controlTokenRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.getPinControlToken(controlTokenRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testGetPinControlTokenWhenBadRequestException() {

        // given
        ControlTokenRequest controlTokenRequest = new ControlTokenRequest();
        when(pinsApi.postPinsControltoken(controlTokenRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.getPinControlToken(controlTokenRequest);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetPinControlTokenWhenInternalServerErrorException() {

        // given
        ControlTokenRequest controlTokenRequest = new ControlTokenRequest();
        when(pinsApi.postPinsControltoken(controlTokenRequest))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.getPinControlToken(controlTokenRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdatePinWhenNotFoundException() {

        // given
        PinRequest pinRequest = new PinRequest();
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND)).when(pinsApi).putPins(pinRequest);

        // when
        marqetaRepository.updatePin(pinRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdatePinWhenBadRequestException() {

        // given
        PinRequest pinRequest = new PinRequest();
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST)).when(pinsApi).putPins(pinRequest);

        // when
        marqetaRepository.updatePin(pinRequest);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testUpdatePinWhenInternalServerErrorException() {

        // given
        PinRequest pinRequest = new PinRequest();
        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR)).when(pinsApi).putPins(pinRequest);

        // when
        marqetaRepository.updatePin(pinRequest);
    }

    @Test(expected = BadRequestException.class)
    public void testUpdatePinWhenPRECONDITION_FAILED() {

        // given
        PinRequest pinRequest = new PinRequest();
        doThrow(new HttpClientErrorException(HttpStatus.PRECONDITION_FAILED)).when(pinsApi).putPins(pinRequest);

        // when
        marqetaRepository.updatePin(pinRequest);
    }

    @Test(expected = NotFoundException.class)
    public void testGetCardLimitsWhenNotFoundException() {

        // given
        when(velocityControlsApi.getVelocitycontrols(CARD_PRODUCT_TOKEN, null, null, null, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        //when
        marqetaRepository.getCardLimits(CARD_PRODUCT_TOKEN);
    }

    @Test(expected = BadRequestException.class)
    public void testGetCardLimitsWhenBadRequestException() {

        // given
        when(velocityControlsApi.getVelocitycontrols(CARD_PRODUCT_TOKEN, null, null, null, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        //when
        marqetaRepository.getCardLimits(CARD_PRODUCT_TOKEN);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetCardLimitsWhenBInternalServerErrorException() {

        // given
        when(velocityControlsApi.getVelocitycontrols(CARD_PRODUCT_TOKEN, null, null, null, null, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        //when
        marqetaRepository.getCardLimits(CARD_PRODUCT_TOKEN);
    }

    @Test(expected = NotFoundException.class)
    public void testUpdateCardLimitsWhenNotFoundException() {

        // given
        VelocityControlUpdateRequest velocityControlUpdateRequest = new VelocityControlUpdateRequest();
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
            .when(velocityControlsApi).putVelocitycontrolsToken(CARD_TOKEN, velocityControlUpdateRequest);

        // when
        marqetaRepository.updateCardLimits(CARD_TOKEN, velocityControlUpdateRequest);

    }

    @Test(expected = BadRequestException.class)
    public void testUpdateCardLimitsWhenBadRequestException() {

        // given
        VelocityControlUpdateRequest velocityControlUpdateRequest = new VelocityControlUpdateRequest();
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
            .when(velocityControlsApi).putVelocitycontrolsToken(CARD_TOKEN, velocityControlUpdateRequest);

        // when
        marqetaRepository.updateCardLimits(CARD_TOKEN, velocityControlUpdateRequest);

    }

    @Test(expected = InternalServerErrorException.class)
    public void testUpdateCardLimitsWhenInternalServerErrorException() {

        // given
        VelocityControlUpdateRequest velocityControlUpdateRequest = new VelocityControlUpdateRequest();
        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
            .when(velocityControlsApi).putVelocitycontrolsToken(CARD_TOKEN, velocityControlUpdateRequest);

        // when
        marqetaRepository.updateCardLimits(CARD_TOKEN, velocityControlUpdateRequest);

    }

    @Test(expected = NotFoundException.class)
    public void testGetCardLimitByIdWhenNotFoundException() {

        // given
        when(velocityControlsApi.getVelocitycontrolsToken(CARD_TOKEN, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.getCardLimitById(CARD_TOKEN);
    }

    @Test(expected = BadRequestException.class)
    public void testGetCardLimitByIdWhenBadRequestException() {

        // given
        when(velocityControlsApi.getVelocitycontrolsToken(CARD_TOKEN, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.getCardLimitById(CARD_TOKEN);
    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetCardLimitByIdWhenInternalServerErrorException() {

        // given
        when(velocityControlsApi.getVelocitycontrolsToken(CARD_TOKEN, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.getCardLimitById(CARD_TOKEN);
    }

    @Test(expected = NotFoundException.class)
    public void testGetCardHolderWhenNotFoundException() {

        // given
        when(usersApi.getUsersToken(USER_TOKEN, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // when
        marqetaRepository.getCardHolder(USER_TOKEN);

    }

    @Test(expected = BadRequestException.class)
    public void testGetCardHolderWhenBadRequestException() {

        // given
        when(usersApi.getUsersToken(USER_TOKEN, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST));

        // when
        marqetaRepository.getCardHolder(USER_TOKEN);

    }

    @Test(expected = InternalServerErrorException.class)
    public void testGetCardHolderWhenInternalServerErrorException() {

        // given
        when(usersApi.getUsersToken(USER_TOKEN, null))
            .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));

        // when
        marqetaRepository.getCardHolder(USER_TOKEN);

    }

    @Test(expected = NotFoundException.class)
    public void testUpdateCardHolderWhenNotFoundException() {

        // given
        UserCardHolderUpdateModel userCardHolderUpdateModel = new UserCardHolderUpdateModel();
        doThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND))
        .when(usersApi).putUsersToken(USER_TOKEN, userCardHolderUpdateModel);

        // when
        marqetaRepository.updateCardHolder(USER_TOKEN, userCardHolderUpdateModel);

    }

    @Test(expected = BadRequestException.class)
    public void testUpdateCardHolderWhenBadRequestException() {

        // given
        UserCardHolderUpdateModel userCardHolderUpdateModel = new UserCardHolderUpdateModel();
        doThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST))
            .when(usersApi).putUsersToken(USER_TOKEN, userCardHolderUpdateModel);

        // when
        marqetaRepository.updateCardHolder(USER_TOKEN, userCardHolderUpdateModel);

    }

    @Test(expected = InternalServerErrorException.class)
    public void testUpdateCardHolderWhenInternalServerErrorException() {

        // given
        UserCardHolderUpdateModel userCardHolderUpdateModel = new UserCardHolderUpdateModel();
        doThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR))
            .when(usersApi).putUsersToken(USER_TOKEN, userCardHolderUpdateModel);

        // when
        marqetaRepository.updateCardHolder(USER_TOKEN, userCardHolderUpdateModel);

    }

}