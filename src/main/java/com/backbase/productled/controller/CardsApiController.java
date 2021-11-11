package com.backbase.productled.controller;

import com.backbase.presentation.card.rest.spec.v2.cards.ActivatePost;
import com.backbase.presentation.card.rest.spec.v2.cards.CardItem;
import com.backbase.presentation.card.rest.spec.v2.cards.CardsApi;
import com.backbase.presentation.card.rest.spec.v2.cards.ChangeLimitsPostItem;
import com.backbase.presentation.card.rest.spec.v2.cards.LockStatusPost;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestPinPost;
import com.backbase.presentation.card.rest.spec.v2.cards.RequestReplacementPost;
import com.backbase.presentation.card.rest.spec.v2.cards.ResetPinPost;
import com.backbase.productled.service.CardsService;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to receive HTTP requests for card management services
 */
@RestController
@AllArgsConstructor
public class CardsApiController implements CardsApi {

    private final CardsService cardsService;

    @Override
    public ResponseEntity<CardItem> getCardById(String id, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(cardsService.getCard(id));
    }

    @Override
    public ResponseEntity<List<CardItem>> getCards(@Valid List<String> ids, @Valid List<String> status,
        @Valid List<String> types, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(
            cardsService.getCards(ids, status, types));
    }

    @Override
    public ResponseEntity<CardItem> updateLockStatus(String id, @Valid LockStatusPost lockStatusPost,
        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(cardsService.postLockStatus(id,
            lockStatusPost.getLockStatus().getValue()));
    }

    @Override
    public ResponseEntity<CardItem> activate(String id, @Valid ActivatePost activatePost,
        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(cardsService.activateCard(id));
    }

    @Override
    public ResponseEntity<CardItem> changeLimits(String id, @Valid List<ChangeLimitsPostItem> changeLimitsPostItem,
        HttpServletRequest httpServletRequest) {
        var limits = changeLimitsPostItem.stream().collect(Collectors.toMap(ChangeLimitsPostItem::getId,
            ChangeLimitsPostItem::getAmount));
        return ResponseEntity.ok(
            cardsService.changeLimits(id, limits));
    }

    @Override
    public ResponseEntity<CardItem> requestPin(String id, @Valid RequestPinPost requestPin,
        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(cardsService.requestPin(id));
    }

    @Override
    public ResponseEntity<CardItem> requestReplacement(String id, @Valid RequestReplacementPost requestReplacementPost,
        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(cardsService.requestReplacement(id));
    }

    @Override
    public ResponseEntity<CardItem> resetPin(String id, @Valid ResetPinPost resetPinPost,
        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(
            cardsService.resetPin(id, resetPinPost));
    }

}
