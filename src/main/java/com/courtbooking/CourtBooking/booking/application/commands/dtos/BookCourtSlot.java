package com.courtbooking.CourtBooking.booking.application.commands.dtos;

import com.courtbooking.CourtBooking.shared.model.Command;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
public class BookCourtSlot extends Command {

    private UUID userId;
    private UUID facilityId;
    private UUID courtId;

    private LocalDate bookingDate;
    private List<UUID> slotDefinitionIds;

    private Integer holdMinutes;  // optional (default 10)
    private String currency;      // optional default "INR"
}
