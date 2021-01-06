package com.backbase.productled.service;

import com.backbase.presentation.card.rest.spec.v2.cards.TravelNotice;
import com.backbase.productled.repository.MarqetaRepository;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class TravelNoticeService {

    private final MarqetaRepository marqetaRepository;

    public TravelNotice createTravelNotice(TravelNotice travelNotice) {
        return null;
    }

    public void deleteTravelNoticeById(String id) {
    }

    public TravelNotice getTravelNoticeById(String id) {
        return null;
    }

    public List<TravelNotice> getTravelNotices() {
        return Collections.emptyList();
    }

    public TravelNotice updateTravelNotice(String id) {
        return null;
    }
}
