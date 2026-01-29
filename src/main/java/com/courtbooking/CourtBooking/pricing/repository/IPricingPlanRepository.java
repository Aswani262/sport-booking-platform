package com.courtbooking.CourtBooking.pricing.repository;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;
import com.courtbooking.CourtBooking.pricing.domain.model.PricingPlan;

import java.time.LocalDate;
import java.util.UUID;

public interface IPricingPlanRepository {
    void save(PricingPlan plan);

    UUID findActiveStandardPlanId(UUID courtId, LocalDate date);


    boolean overlapsWithActivePlansSameOrHigherPriority(AddPricingPlan cmd);

    boolean existsActiveStandardWholeDayAllDays(UUID courtId, LocalDate date);
}
