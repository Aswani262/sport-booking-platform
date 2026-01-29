package com.courtbooking.CourtBooking.pricing.domain.exceptions;

import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

public class PricingPlanOverlapsException extends BusinessValidationException {
    public PricingPlanOverlapsException() {
        super("pricing_plan_overlaps", "Pricing plan overlaps with an existing active plan of same or higher priority.", HttpStatus.CONFLICT);
    }
}
