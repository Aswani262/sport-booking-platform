package com.courtbooking.CourtBooking.pricing.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SlotDefView {
    private UUID slotDefinitionId;
    private UUID courtId;

    private Integer dayOfWeek;     // nullable
    private String dayType;        // nullable WEEKDAY/WEEKEND

    private LocalTime startTime;
    private LocalTime endTime;

    private LocalDate effectiveFrom;
    private LocalDate effectiveTo; // nullable

    private UUID pricePlanId;
}
