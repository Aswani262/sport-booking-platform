package com.courtbooking.CourtBooking.booking.service;

import com.courtbooking.CourtBooking.pricing.api.dtos.SlotPricingDto;
import com.courtbooking.CourtBooking.pricing.application.query.dtos.LoadSlotPricingQuery;
import com.courtbooking.CourtBooking.pricing.application.query.handler.LoadSlotPricingUseCase;
import com.courtbooking.CourtBooking.shared.annoation.IntegrationService;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@IntegrationService
@RequiredArgsConstructor
public class PricingIntegrationService {

    private final LoadSlotPricingUseCase loadSlotPricingUseCase;

    public List<SlotPricingDto> loadSlotsWithPricing(UUID courtId, LocalDate bookingDate, List<UUID> slotDefinitionIds) {
        return loadSlotPricingUseCase.handle(new LoadSlotPricingQuery(courtId, bookingDate, slotDefinitionIds));
    }
}
