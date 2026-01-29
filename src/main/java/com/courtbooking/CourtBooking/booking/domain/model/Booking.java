package com.courtbooking.CourtBooking.booking.domain.model;

import com.courtbooking.CourtBooking.shared.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
//it worked as aggregate root for booking process by encapsulating all related entities and ensuring consistency

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Booking extends BaseEntity {

    private UUID bookingId;

    private UUID userId;
    private UUID facilityId;
    private UUID courtId;

    private OffsetDateTime bookingDate;

    private BookingStatus status;           // PAYMENT_PENDING, CONFIRMED, CANCELLED, EXPIRED

    //Because bookings, holds, payments, and audits are time-sensitive and timezone-critical, and OffsetDateTime prevents subtle production bugs.
   // OffsetDateTime = Date + Time + Timezone Offset , always stored in UTC in DB
    private OffsetDateTime holdExpiresAt;   // for 10 min hold window
    private BookingType bookingType;       // ONE_TIME, RECURRING
    private BigDecimal totalAmount;
    private String currency;// INR, USD etc.
    private String paymentTransactionId;
    public enum BookingStatus {
        PAYMENT_PENDING,// initial status when slots are held
        CONFIRMED,
        CANCELLED,
        EXPIRED
    }
}
