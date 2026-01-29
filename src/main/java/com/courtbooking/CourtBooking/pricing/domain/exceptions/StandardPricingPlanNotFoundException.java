package com.courtbooking.CourtBooking.pricing.domain.exceptions;

import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class StandardPricingPlanNotFoundException extends BusinessValidationException {
    public StandardPricingPlanNotFoundException(UUID courtId) {
        super(
                "standard_pricing_plan_not_found",
                "Standard pricing plan not found for court: " + courtId,
                HttpStatus.CONFLICT
        );
    }
}
