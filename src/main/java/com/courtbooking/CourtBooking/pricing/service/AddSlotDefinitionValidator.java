package com.courtbooking.CourtBooking.pricing.service;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class AddSlotDefinitionValidator {

    public Notification validate(AddSlotDefinition cmd) {
        Notification n = new Notification();

        if (cmd == null) {
            n.add("command_null", "command", "Request body cannot be null");
            return n;
        }

        if (cmd.getFacilityId() == null) n.add("facility_id_required", "facilityId", "facilityId is required");
        if (cmd.getCourtId() == null) n.add("court_id_required", "courtId", "courtId is required");

        // dayOfWeek OR dayType required
        boolean hasDow = cmd.getDayOfWeek() != null;
        boolean hasDayType = cmd.getDayType() != null && !cmd.getDayType().trim().isEmpty();
        if (!hasDow && !hasDayType) {
            n.add("day_pattern_required", "dayPattern", "Either dayOfWeek or dayType must be provided");
        }

        // dayOfWeek range
        if (cmd.getDayOfWeek() != null && (cmd.getDayOfWeek() < 1 || cmd.getDayOfWeek() > 7)) {
            n.add("day_of_week_invalid", "dayOfWeek", "dayOfWeek must be 1..7");
        }

        // time
        LocalTime st = cmd.getStartTime();
        LocalTime et = cmd.getEndTime();

        if (st == null) n.add("start_time_required", "startTime", "startTime is required");
        if (et == null) n.add("end_time_required", "endTime", "endTime is required");

        if (st != null && et != null && !st.isBefore(et)) {
            n.add("time_range_invalid", "timeRange", "startTime must be before endTime");
        }

        // effective dates
        if (cmd.getEffectiveFrom() == null) n.add("effective_from_required", "effectiveFrom", "effectiveFrom is required");

        if (cmd.getEffectiveFrom() != null && cmd.getEffectiveTo() != null &&
                cmd.getEffectiveTo().isBefore(cmd.getEffectiveFrom())) {
            n.add("effective_range_invalid", "effectiveTo", "effectiveTo must be >= effectiveFrom");
        }

        // dayType enum validation (if present)
        if (hasDayType) {
            String dt = cmd.getDayType().trim().toUpperCase();
            if (!dt.equals("WEEKDAY") && !dt.equals("WEEKEND")) {
                n.add("day_type_invalid", "dayType", "dayType must be WEEKDAY or WEEKEND");
            }
        }

        // Optional rule: don't allow both dayOfWeek and dayType together (avoid ambiguity)
        if (hasDow && hasDayType) {
            n.add("day_pattern_ambiguous", "dayPattern", "Provide either dayOfWeek or dayType, not both");
        }

        return n;
    }
}
