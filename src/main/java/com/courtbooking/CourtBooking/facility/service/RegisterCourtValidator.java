package com.courtbooking.CourtBooking.facility.service;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterCourt;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class RegisterCourtValidator {

    public Notification validate(RegisterCourt cmd) {
        Notification n = new Notification();

        if (cmd == null) {
            n.add("command_null", "command", "Request body cannot be null");
            return n;
        }

        if (cmd.getFacilityId() == null) {
            n.add("facility_id_required", "facilityId", "facilityId is required");
        }

        if (isBlank(cmd.getSport())) {
            n.add("sport_required", "sport", "sport is required");
        }

        if (isBlank(cmd.getName())) {
            n.add("name_required", "name", "court name is required");
        } else {
            String name = cmd.getName().trim();
            if (name.length() < 2 || name.length() > 120) {
                n.add("name_length_invalid", "name", "court name must be 2 to 120 characters");
            }
        }

        LocalTime open = cmd.getOpeningTime();
        LocalTime close = cmd.getClosingTime();

        if (open == null) n.add("opening_time_required", "openingTime", "openingTime is required");
        if (close == null) n.add("closing_time_required", "closingTime", "closingTime is required");

        if (open != null && close != null && !open.isBefore(close)) {
            n.add("operating_hours_invalid", "operatingHours", "openingTime must be before closingTime");
        }

        if (cmd.getMinBookingMinutes() == null) {
            n.add("min_booking_minutes_required", "minBookingMinutes", "minBookingMinutes is required");
        } else if (cmd.getMinBookingMinutes() <= 0) {
            n.add("min_booking_minutes_invalid", "minBookingMinutes", "minBookingMinutes must be > 0");
        } else if (cmd.getMinBookingMinutes() % 15 != 0) {
            n.add("min_booking_minutes_step_invalid", "minBookingMinutes", "minBookingMinutes must be multiple of 15");
        }

        if (cmd.getCapacity() != null && cmd.getCapacity() <= 0) {
            n.add("capacity_invalid", "capacity", "capacity must be > 0");
        }

        return n;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
