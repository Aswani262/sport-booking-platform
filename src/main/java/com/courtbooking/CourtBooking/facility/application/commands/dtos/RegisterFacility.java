package com.courtbooking.CourtBooking.facility.application.commands.dtos;


import com.courtbooking.CourtBooking.shared.model.Command;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class RegisterFacility extends Command {
    private UUID ownerUserId;
    private UUID adminUserId; // optional

    private String name;
    private String description;

    private String contactPhone;
    private String contactEmail;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;

    private Double latitude;
    private Double longitude;

    private LocalTime openingTime;
    private LocalTime closingTime;
}
