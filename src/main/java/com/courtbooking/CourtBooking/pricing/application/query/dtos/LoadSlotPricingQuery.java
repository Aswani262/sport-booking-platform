package com.courtbooking.CourtBooking.pricing.application.query.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
public class LoadSlotPricingQuery {
    private UUID courtId;
    private LocalDate bookingDate;
    private List<UUID> slotDefinitionIds; // optional: if null/empty return all valid slots for date
}
