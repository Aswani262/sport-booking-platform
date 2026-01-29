package com.courtbooking.CourtBooking.facility.repository;

import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CourtReadRepository implements ICourtReadRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public Integer findMinBookingMinutes(UUID courtId) {
        String sql = "SELECT min_booking_minutes FROM court WHERE court_id = :courtId AND deleted = false";
        List<Integer> rows = jdbc.query(sql, Map.of("courtId", courtId),
                (rs, i) -> (Integer) rs.getObject("min_booking_minutes"));
        return rows.isEmpty() ? null : rows.get(0);
    }
}
