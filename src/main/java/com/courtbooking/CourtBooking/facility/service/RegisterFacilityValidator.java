package com.courtbooking.CourtBooking.facility.service;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterFacility;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.regex.Pattern;

@Component
public class RegisterFacilityValidator {

    private static final Pattern EMAIL =
            Pattern.compile("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$");

    public Notification validate(RegisterFacility cmd) {
        Notification n = new Notification();

        if (cmd == null) {
            n.add("command_null", "command", "Request body cannot be null");
            return n;
        }

        // owner
        if (cmd.getOwnerUserId() == null) {
            n.add("owner_user_id_required", "ownerUserId", "ownerUserId is required");
        }

        // name
        if (isBlank(cmd.getName())) {
            n.add("name_required", "name", "Facility name is required");
        } else {
            String name = cmd.getName().trim();
            if (name.length() < 3 || name.length() > 120) {
                n.add("name_length_invalid", "name", "Facility name must be 3 to 120 characters");
            }
        }

        // contact
        boolean phoneBlank = isBlank(cmd.getContactPhone());
        boolean emailBlank = isBlank(cmd.getContactEmail());

        if (phoneBlank && emailBlank) {
            n.add("contact_required", "contact", "Either contactPhone or contactEmail must be provided");
        }

        if (!emailBlank && !EMAIL.matcher(cmd.getContactEmail().trim()).matches()) {
            n.add("contact_email_invalid", "contactEmail", "Invalid email format");
        }

        if (!phoneBlank && cmd.getContactPhone().trim().length() < 8) {
            n.add("contact_phone_invalid", "contactPhone", "Invalid phone number");
        }

        // address
        require(n, cmd.getAddressLine1(), "addressLine1", "address_line1_required");
        require(n, cmd.getCity(), "city", "city_required");
        require(n, cmd.getState(), "state", "state_required");
        require(n, cmd.getCountry(), "country", "country_required");
        require(n, cmd.getPincode(), "pincode", "pincode_required");

        if (!isBlank(cmd.getPincode())) {
            String pin = cmd.getPincode().trim();
            if (pin.length() < 4 || pin.length() > 10) {
                n.add("pincode_length_invalid", "pincode", "Pincode must be 4 to 10 characters");
            }
        }

        // geo
        if (cmd.getLatitude() == null) {
            n.add("latitude_required", "latitude", "Latitude is required");
        } else if (cmd.getLatitude() < -90 || cmd.getLatitude() > 90) {
            n.add("latitude_invalid", "latitude", "Latitude must be between -90 and 90");
        }

        if (cmd.getLongitude() == null) {
            n.add("longitude_required", "longitude", "Longitude is required");
        } else if (cmd.getLongitude() < -180 || cmd.getLongitude() > 180) {
            n.add("longitude_invalid", "longitude", "Longitude must be between -180 and 180");
        }

        // operating hours
        LocalTime open = cmd.getOpeningTime();
        LocalTime close = cmd.getClosingTime();

        if (open == null) n.add("opening_time_required", "openingTime", "openingTime is required");
        if (close == null) n.add("closing_time_required", "closingTime", "closingTime is required");

        if (open != null && close != null && !open.isBefore(close)) {
            n.add("operating_hours_invalid", "operatingHours", "openingTime must be before closingTime");
        }

        return n;
    }

    private static void require(Notification n, String value, String field, String code) {
        if (isBlank(value)) n.add(code, field, field + " is required");
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
