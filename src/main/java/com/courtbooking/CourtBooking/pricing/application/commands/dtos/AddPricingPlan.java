package com.courtbooking.CourtBooking.pricing.application.commands.dtos;

import com.courtbooking.CourtBooking.shared.model.Command;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
public class AddPricingPlan extends Command {

    private UUID facilityId;
    private UUID courtId;

    private String name;        // Standard, Peak Hours
    private String currency;    // INR

    private LocalDate validFrom;
    private LocalDate validTo;  // nullable => open-ended (inclusive if present)

    private Integer dayOfWeek;   // 1..7 or null(all)
    private LocalTime startTime; // nullable
    private LocalTime endTime;   // nullable

    private BigDecimal pricePerUnit;

    private String planType;     // STANDARD, PEAK
    private String priority;    // higher wins
}
