package com.courtbooking.CourtBooking.booking.domain.model;

import com.courtbooking.CourtBooking.shared.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
//We are going to put unique constraints on (court_id, slot_definition_id, booking_date) to prevent double booking of same slot
public class BookingItem extends BaseEntity {

    private UUID bookingItemId;

    private UUID bookingId;
    private UUID courtId;
    private UUID slotDefinitionId;   // FK -> slot_definition.slot_definition_id

    private LocalDate bookingDate;   // date for which this slot is booked

    private BookingItemStatus status; // HELD, CONFIRMED, CANCELLED, EXPIRED

    private UUID priceRuleId;        // audit reference only
    private BigDecimal finalPrice;   // MUST store snapshot price

    // ---------- Audit ----------
    private OffsetDateTime createdAt;

    public enum BookingItemStatus {
        HELD,
        CONFIRMED,
        CANCELLED,
        EXPIRED
    }
}
