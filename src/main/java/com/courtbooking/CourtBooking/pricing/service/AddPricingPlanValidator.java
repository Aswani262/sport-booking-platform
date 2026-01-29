package com.courtbooking.CourtBooking.pricing.service;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalTime;

@Component
public class AddPricingPlanValidator {

    public Notification validate(AddPricingPlan cmd) {
        Notification n = new Notification();

        if (cmd == null) {
            n.add("command_null", "command", "Request body cannot be null");
            return n;
        }

        if (cmd.getFacilityId() == null) n.add("facility_id_required", "facilityId", "facilityId is required");
        if (cmd.getCourtId() == null) n.add("court_id_required", "courtId", "courtId is required");

        if (isBlank(cmd.getName())) n.add("name_required", "name", "name is required");
        if (isBlank(cmd.getCurrency())) n.add("currency_required", "currency", "currency is required");

        if (cmd.getValidFrom() == null) n.add("valid_from_required", "validFrom", "validFrom is required");

        if (cmd.getValidFrom() != null && cmd.getValidTo() != null && cmd.getValidTo().isBefore(cmd.getValidFrom())) {
            n.add("valid_range_invalid", "validTo", "validTo must be >= validFrom");
        }

        // dayOfWeek: 1..7 or null
        if (cmd.getDayOfWeek() != null && (cmd.getDayOfWeek() < 1 || cmd.getDayOfWeek() > 7)) {
            n.add("day_of_week_invalid", "dayOfWeek", "dayOfWeek must be 1..7");
        }

        // time window: either both null (whole day) or both present (range)
        LocalTime st = cmd.getStartTime();
        LocalTime et = cmd.getEndTime();

        if ((st == null) != (et == null)) {
            n.add("time_window_incomplete", "timeWindow", "startTime and endTime must both be provided or both be null");
        }
        if (st != null && et != null && !st.isBefore(et)) {
            n.add("time_window_invalid", "timeWindow", "startTime must be before endTime");
        }

        if (cmd.getPricePerUnit() == null) {
            n.add("price_per_unit_required", "pricePerUnit", "pricePerUnit is required");
        } else if (cmd.getPricePerUnit().signum() <= 0) {
            n.add("price_per_unit_invalid", "pricePerUnit", "pricePerUnit must be > 0");
        }

        if (isBlank(cmd.getPlanType())) n.add("plan_type_required", "planType", "planType is required");
        if (cmd.getPriority() == null) n.add("priority_required", "priority", "priority is required");

        return n;
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
