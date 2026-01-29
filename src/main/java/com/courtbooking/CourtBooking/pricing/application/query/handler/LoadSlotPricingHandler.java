package com.courtbooking.CourtBooking.pricing.application.query.handler;

import com.courtbooking.CourtBooking.pricing.api.dtos.SlotPricingDto;
import com.courtbooking.CourtBooking.pricing.application.query.dtos.LoadSlotPricingQuery;
import com.courtbooking.CourtBooking.shared.annoation.ApplicationService;
import lombok.RequiredArgsConstructor;

import java.util.List;

@ApplicationService
@RequiredArgsConstructor
public class LoadSlotPricingHandler implements LoadSlotPricingUseCase {

    private final PricingQueryRepository repo;

    @Override
    public List<SlotPricingDto> handle(LoadSlotPricingQuery query) {
        // repository will ensure effectiveFrom/effectiveTo and active = true, and join pricing_plan
        return repo.loadSlotPricing(query.getCourtId(), query.getBookingDate(), query.getSlotDefinitionIds());
    }
}
