package com.courtbooking.CourtBooking.facility.application.commands.dtos;


import com.courtbooking.CourtBooking.shared.model.Command;
import lombok.Data;

import java.time.LocalTime;
import java.util.UUID;

@Data
public class RegisterCourt extends Command {
    private UUID facilityId;

    private String sport;              // or SportType if you want, but keep String at API boundary
    private String name;
    private String surfaceType;
    private Integer capacity;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private Integer minBookingMinutes; // 30/60 etc.
}
