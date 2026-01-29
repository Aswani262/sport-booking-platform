package com.courtbooking.CourtBooking.booking.domain.exceptions;

import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

import java.util.UUID;

public class CourtNotFoundException extends BusinessValidationException {
    public CourtNotFoundException(UUID courtId) {
        super("court_not_found", "Court not found: " + courtId, HttpStatus.CONFLICT);
    }
}
