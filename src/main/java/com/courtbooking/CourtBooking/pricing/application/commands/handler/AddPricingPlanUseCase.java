package com.courtbooking.CourtBooking.pricing.application.commands.handler;


import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;

import java.util.UUID;

public interface AddPricingPlanUseCase {
    UUID handle(AddPricingPlan command);
}
