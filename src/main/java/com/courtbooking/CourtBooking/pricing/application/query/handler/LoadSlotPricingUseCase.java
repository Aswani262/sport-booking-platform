package com.courtbooking.CourtBooking.pricing.application.query.handler;


import com.courtbooking.CourtBooking.pricing.api.dtos.SlotPricingDto;
import com.courtbooking.CourtBooking.pricing.application.query.dtos.LoadSlotPricingQuery;

import java.util.List;

public interface LoadSlotPricingUseCase {
    List<SlotPricingDto> handle(LoadSlotPricingQuery query);
}
