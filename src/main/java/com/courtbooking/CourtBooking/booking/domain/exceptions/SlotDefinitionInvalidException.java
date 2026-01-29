package com.courtbooking.CourtBooking.booking.domain.exceptions;

import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

public class SlotDefinitionInvalidException extends BusinessValidationException {
    public SlotDefinitionInvalidException(String message) {
        super("slot_definition_invalid", message, HttpStatus.CONFLICT);
    }
}
