package com.courtbooking.CourtBooking.booking.repository;

import com.courtbooking.CourtBooking.booking.domain.model.Booking;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingRepository implements IBookingRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void save(Booking b) {
        String sql = """
            INSERT INTO booking(
              booking_id, user_id, facility_id, court_id,
              booking_date, status, hold_expires_at,
              booking_type, total_amount, currency, payment_transaction_id,
              created_at, updated_at
            )
            VALUES(
              :bookingId, :userId, :facilityId, :courtId,
              :bookingDate, :status, :holdExpiresAt,
              :bookingType, :totalAmount, :currency, :paymentTransactionId,
              now(), now()
            )
            """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("bookingId", b.getBookingId())
                .addValue("userId", b.getUserId())
                .addValue("facilityId", b.getFacilityId())
                .addValue("courtId", b.getCourtId())
                .addValue("bookingDate", b.getBookingDate())
                .addValue("status", b.getStatus().name())
                .addValue("holdExpiresAt", b.getHoldExpiresAt())
                .addValue("bookingType", b.getBookingType().name())
                .addValue("totalAmount", b.getTotalAmount())
                .addValue("currency", b.getCurrency())
                .addValue("paymentTransactionId", b.getPaymentTransactionId());

        jdbc.update(sql, ps);
    }
}
