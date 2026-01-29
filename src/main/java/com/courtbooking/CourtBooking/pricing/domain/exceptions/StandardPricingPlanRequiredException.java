package com.courtbooking.CourtBooking.pricing.domain.exceptions;

import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

public class StandardPricingPlanRequiredException extends BusinessValidationException {
    public StandardPricingPlanRequiredException() {
        super("standard_pricing_plan_required", "Court must have at least one STANDARD pricing plan applicable for all days and whole day.", HttpStatus.CONFLICT);
    }
}
