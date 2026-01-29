package com.courtbooking.CourtBooking.pricing.service;


import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;

public interface IPricingPlanService {
    void validateBusinessRules(AddPricingPlan cmd);
}
