package com.backbase.productled.controller;

import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.presentation.card.rest.spec.v2.cards.TravelNoticesApi;
import com.backbase.productled.service.TravelNoticeService;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller class to receive HTTP requests for managing travel notices
 */
@RestController
@AllArgsConstructor
public class TravelNoticesApiController implements TravelNoticesApi {

    private final TravelNoticeService travelNoticeService;

    @Override
    public ResponseEntity<TravelNotice> createTravelNotice(@Valid TravelNotice travelNotice,
        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(travelNoticeService.createTravelNotice(travelNotice));
    }

    @Override
    public ResponseEntity<Void> deleteTravelNoticeById(String id, HttpServletRequest httpServletRequest) {
        travelNoticeService.deleteTravelNoticeById(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<TravelNotice> getTravelNoticeById(String id, HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(travelNoticeService.getTravelNoticeById(id));
    }

    @Override
    public ResponseEntity<List<TravelNotice>> getTravelNotices(HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(travelNoticeService.getTravelNotices());
    }

    @Override
    public ResponseEntity<TravelNotice> updateTravelNotice(String id, @Valid TravelNotice travelNotice,
        HttpServletRequest httpServletRequest) {
        return ResponseEntity.ok(travelNoticeService.updateTravelNotice(id, travelNotice));
    }
}
