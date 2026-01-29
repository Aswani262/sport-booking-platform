package com.courtbooking.CourtBooking.facility.domain.exceptions;


import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

public class FacilityAlreadyExistsException extends BusinessValidationException {

    public FacilityAlreadyExistsException(String name, String city) {
        super(
                "facility_already_exists",
                "Facility already exists with name '" + name + "' in city '" + city + "'.",
                HttpStatus.CONFLICT // 409
        );
    }
}
