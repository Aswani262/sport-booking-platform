package com.courtbooking.CourtBooking.booking.service;

import com.courtbooking.CourtBooking.booking.application.commands.dtos.BookCourtSlot;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.HashSet;

@Component
public class BookCourtSlotValidator {

    public Notification validate(BookCourtSlot cmd) {
        Notification n = new Notification();

        if (cmd == null) {
            n.add("command_null", "command", "Request body cannot be null");
            return n;
        }

        if (cmd.getUserId() == null) n.add("user_id_required", "userId", "userId is required");
        if (cmd.getFacilityId() == null) n.add("facility_id_required", "facilityId", "facilityId is required");
        if (cmd.getCourtId() == null) n.add("court_id_required", "courtId", "courtId is required");

        if (cmd.getBookingDate() == null) {
            n.add("booking_date_required", "bookingDate", "bookingDate is required");
        } else if (cmd.getBookingDate().isBefore(LocalDate.now())) {
            n.add("booking_date_past", "bookingDate", "bookingDate cannot be in the past");
        }

        if (cmd.getSlotDefinitionIds() == null || cmd.getSlotDefinitionIds().isEmpty()) {
            n.add("slot_definitions_required", "slotDefinitionIds", "At least one slotDefinitionId is required");
        } else if (cmd.getSlotDefinitionIds().size() != new HashSet<>(cmd.getSlotDefinitionIds()).size()) {
            n.add("slot_definitions_duplicate", "slotDefinitionIds", "Duplicate slotDefinitionId found");
        }

        if (cmd.getHoldMinutes() != null && (cmd.getHoldMinutes() < 1 || cmd.getHoldMinutes() > 60)) {
            n.add("hold_minutes_invalid", "holdMinutes", "holdMinutes must be between 1 and 60");
        }

        if (cmd.getCurrency() != null && cmd.getCurrency().trim().length() != 3) {
            n.add("currency_invalid", "currency", "currency must be 3 letters (e.g. INR)");
        }

        return n;
    }
}
