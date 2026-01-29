package com.courtbooking.CourtBooking.booking.domain.exceptions;

import com.courtbooking.CourtBooking.shared.exception.BusinessValidationException;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;
import java.util.UUID;

public class SlotAlreadyBookedException extends BusinessValidationException {
    public SlotAlreadyBookedException(UUID slotDefinitionId, LocalDate bookingDate) {
        super(
                "slot_already_booked",
                "Slot already booked/held for date. slotDefinitionId=" + slotDefinitionId + ", bookingDate=" + bookingDate,
                HttpStatus.CONFLICT
        );
    }
}
