package com.backbase.productled.controller;

import com.backbase.presentation.card.api.service.v2.CardsApi;
import com.backbase.presentation.card.api.service.v2.model.ActivatePost;
import com.backbase.presentation.card.api.service.v2.model.CardItem;
import com.backbase.presentation.card.api.service.v2.model.ChangeLimitsPostItem;
import com.backbase.presentation.card.api.service.v2.model.LockStatusPost;
import com.backbase.presentation.card.api.service.v2.model.RequestReplacementPost;
import com.backbase.productled.mapper.CardsMappers;
import com.backbase.productled.service.CardsService;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@AllArgsConstructor
@RestController
public class CardsServiceApiController implements CardsApi {

    private final CardsService cardsService;
    private final CardsMappers cardsMappers;

    @Override
    public ResponseEntity<CardItem> activateByUser(String userId, String id, ActivatePost activatePost,
        HttpServletRequest httpServletRequest) {
        var cardItem = cardsService.activateCard(id);
        return ResponseEntity.ok(cardsMappers.mapToServiceCardItem(cardItem));
    }

    @Override
    public ResponseEntity<CardItem> changeLimitsByUser(String userId, String id,
        List<ChangeLimitsPostItem> changeLimitsPostItem, HttpServletRequest httpServletRequest) {
        var limits = changeLimitsPostItem.stream().collect(Collectors.toMap(ChangeLimitsPostItem::getId,
            ChangeLimitsPostItem::getAmount));
        var cardItem = cardsService.changeLimits(userId, id, limits);
        return ResponseEntity.ok(cardsMappers.mapToServiceCardItem(cardItem));
    }

    @Override
    public ResponseEntity<CardItem> getCardByIdAndUser(String userId, String id,
        HttpServletRequest httpServletRequest) {
        var cardItem = cardsService.getCard(userId, id);
        return ResponseEntity.ok(cardsMappers.mapToServiceCardItem(cardItem));
    }

    @Override
    public ResponseEntity<List<CardItem>> getCardsByUser(String userId, List<String> ids, List<String> status,
        List<String> types, HttpServletRequest httpServletRequest) {
        var cardItems = cardsService.getCards(userId, ids, status, types);
        return ResponseEntity.ok(cardItems.stream().map(cardsMappers::mapToServiceCardItem).collect(Collectors.toList()));
    }

    @Override
    public ResponseEntity<CardItem> requestReplacementByUser(String userId, String id,
        RequestReplacementPost requestReplacementPost, HttpServletRequest httpServletRequest) {
        var cardItem = cardsService.requestReplacement(userId, id);
        return ResponseEntity.ok(cardsMappers.mapToServiceCardItem(cardItem));
    }

    @Override
    public ResponseEntity<CardItem> updateLockStatusByUser(String userId, String id, LockStatusPost lockStatusPost,
        HttpServletRequest httpServletRequest) {
        var cardItem = cardsService.postLockStatus(userId, id, lockStatusPost.getLockStatus().getValue());
        return ResponseEntity.ok(cardsMappers.mapToServiceCardItem(cardItem));
    }
}
