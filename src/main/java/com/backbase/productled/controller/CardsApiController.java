package com.backbase.productled.controller;

import com.backbase.presentation.card.rest.spec.v2.cards.CardsApi;
import com.backbase.presentation.card.rest.spec.v2.cards.IdactivationPostRequestBody;
import com.backbase.presentation.card.rest.spec.v2.cards.IdlockstatusPostRequestBody;
import com.backbase.presentation.card.rest.spec.v2.cards.IdpinrequestPostRequestBody;
import com.backbase.presentation.card.rest.spec.v2.cards.IdpinresetPostRequestBody;
import com.backbase.presentation.card.rest.spec.v2.cards.IdreplacementPostRequestBody;
import com.backbase.presentation.card.spec.v2.cards.CardItem;
import com.backbase.presentation.card.spec.v2.cards.ChangeLimitsPostItem;
import com.backbase.productled.service.CardsService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
public class CardsApiController implements CardsApi {

    private final CardsService cardsService;

    @Override
    public List<CardItem> getCards(String[] ids, String[] status, String[] types, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {
        return cardsService.getCards(ids, status, types);
    }

    @Override
    public CardItem getId(String id, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return cardsService.getCard(id);
    }

    @Override
    public CardItem postIdlockstatus(@Valid IdlockstatusPostRequestBody idlockstatusPostRequestBody,
        BindingResult bindingResult, String id, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {
        return cardsService.postLockStatus(id, idlockstatusPostRequestBody);
    }

    @Override
    public CardItem postIdreplacement(@Valid IdreplacementPostRequestBody idreplacementPostRequestBody,
        BindingResult bindingResult, String id, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    public CardItem postIdactivation(@Valid IdactivationPostRequestBody idactivationPostRequestBody,
        BindingResult bindingResult, String id, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {
        return cardsService.activateCard(id);
    }

    @Override
    public CardItem postIdpinreset(@Valid IdpinresetPostRequestBody idpinresetPostRequestBody,
        BindingResult bindingResult, String id, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    public CardItem postIdpinrequest(@Valid IdpinrequestPostRequestBody idpinrequestPostRequestBody,
        BindingResult bindingResult, String id, HttpServletRequest httpServletRequest,
        HttpServletResponse httpServletResponse) {
        return null;
    }

    @Override
    public CardItem postIdlimits(@Valid List<ChangeLimitsPostItem> changeLimitsPostItem, BindingResult bindingResult,
        String id, HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        return null;
    }
}
