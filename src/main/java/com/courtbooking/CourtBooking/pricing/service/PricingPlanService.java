package com.courtbooking.CourtBooking.pricing.service;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;
import com.courtbooking.CourtBooking.pricing.domain.exceptions.PricingPlanOverlapsException;
import com.courtbooking.CourtBooking.pricing.domain.exceptions.StandardPricingPlanRequiredException;
import com.courtbooking.CourtBooking.pricing.repository.IPricingPlanRepository;
import com.courtbooking.CourtBooking.shared.annoation.DomainService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class PricingPlanService implements IPricingPlanService {

    private final IPricingPlanRepository repo;

    @Override
    public void validateBusinessRules(AddPricingPlan cmd) {

        // Rule 1: If creating a non-standard plan, court must already have at least one STANDARD plan
        String planType = cmd.getPlanType().trim().toUpperCase();
        if (!"STANDARD".equals(planType)) {
            boolean hasStandard = repo.existsActiveStandardWholeDayAllDays(cmd.getCourtId(), cmd.getValidFrom());
            if (!hasStandard) {
                // You can enable this rule if you want strict enforcement:
                 throw new StandardPricingPlanRequiredException();
            }
        }

        // Rule 2: Overlap check against active plans of same/higher priority
        boolean overlaps = repo.overlapsWithActivePlansSameOrHigherPriority(cmd);
        if (overlaps) {
            throw new PricingPlanOverlapsException();
        }
    }
}
