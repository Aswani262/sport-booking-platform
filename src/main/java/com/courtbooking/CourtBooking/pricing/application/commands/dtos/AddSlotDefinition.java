package com.courtbooking.CourtBooking.pricing.application.commands.dtos;

import com.courtbooking.CourtBooking.shared.model.Command;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AddSlotDefinition extends Command {

    private UUID facilityId;
    private UUID courtId;

    private Integer dayOfWeek; // 1..7 nullable
    private String dayType;    // WEEKDAY/WEEKEND nullable (API boundary as String)

    private LocalTime startTime;
    private LocalTime endTime;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo; // nullable

    private String description;

    private UUID pricePlanId; // nullable => default to standard pricing plan for that court
}
