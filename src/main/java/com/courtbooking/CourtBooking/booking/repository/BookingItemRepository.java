package com.courtbooking.CourtBooking.booking.repository;

import com.courtbooking.CourtBooking.booking.domain.model.BookingItem;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
@RequiredArgsConstructor
public class BookingItemRepository implements IBookingItemRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void insertHeldItem(BookingItem i) {
        String sql = """
            INSERT INTO booking_item(
              booking_item_id,
              booking_id, court_id, slot_definition_id,
              booking_date, status,
              price_rule_id, final_price,
              created_at
            )
            VALUES(
              :bookingItemId,
              :bookingId, :courtId, :slotDefinitionId,
              :bookingDate, :status,
              :priceRuleId, :finalPrice,
              now()
            )
            """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("bookingItemId", i.getBookingItemId())
                .addValue("bookingId", i.getBookingId())
                .addValue("courtId", i.getCourtId())
                .addValue("slotDefinitionId", i.getSlotDefinitionId())
                .addValue("bookingDate", i.getBookingDate())
                .addValue("status", i.getStatus().name())
                .addValue("priceRuleId", i.getPriceRuleId())
                .addValue("finalPrice", i.getFinalPrice());

        // Let caller catch DuplicateKeyException and translate to business error
        jdbc.update(sql, ps);
    }
}
