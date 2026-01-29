package com.courtbooking.CourtBooking.pricing.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Data
@AllArgsConstructor
public class SlotPricingDto {
    private UUID slotDefinitionId;
    private UUID courtId;

    private LocalTime startTime;
    private LocalTime endTime;

    private Integer dayOfWeek;     // nullable
    private String dayType;        // nullable WEEKDAY/WEEKEND

    private UUID pricingPlanId;
    private BigDecimal pricePerUnit;
    private String currency;
}
