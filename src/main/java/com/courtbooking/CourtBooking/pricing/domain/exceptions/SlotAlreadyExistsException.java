package com.courtbooking.CourtBooking.pricing.domain.exceptions;

import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

public class SlotAlreadyExistsException extends BusinessValidationException {
    public SlotAlreadyExistsException() {
        super(
                "slot_definition_already_exists",
                "Slot definition already exists for this court and time pattern.",
                HttpStatus.CONFLICT
        );
    }
}
