package com.courtbooking.CourtBooking.facility.domain.exceptions;


import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

public class CourtAlreadyExistsException extends BusinessValidationException {

    public CourtAlreadyExistsException(String courtName) {
        super(
                "court_already_exists",
                "Court already exists with name '" + courtName + "' in this facility.",
                HttpStatus.CONFLICT
        );
    }
}
