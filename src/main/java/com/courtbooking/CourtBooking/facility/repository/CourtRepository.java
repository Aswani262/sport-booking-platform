package com.courtbooking.CourtBooking.facility.repository;


import com.courtbooking.CourtBooking.facility.domain.model.Court;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class CourtRepository implements ICourtRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void save(Court c) {
        String sql = """
            INSERT INTO court(
              court_id, facility_id,
              sport, name, surface_type, capacity,
              opening_time, closing_time, min_booking_minutes,
              court_status, deleted,
              created_at, updated_at
            )
            VALUES(
              :courtId, :facilityId,
              :sport, :name, :surfaceType, :capacity,
              :openingTime, :closingTime, :minBookingMinutes,
              :courtStatus, false,
              now(), now()
            )
            """;

        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("courtId", c.getCourtId())
                .addValue("facilityId", c.getFacilityId())
                .addValue("sportType", c.getSportType().name())
                .addValue("name", c.getName())
                .addValue("surfaceType", c.getSurfaceType())
                .addValue("capacity", c.getCapacity())
                .addValue("openingTime", c.getOpeningTime())
                .addValue("closingTime", c.getClosingTime())
                .addValue("minBookingMinutes", c.getMinBookingMinutes())
                .addValue("courtStatus", c.getCourtStatus().name());

        jdbc.update(sql, p);
    }

    @Override
    public boolean existsActiveByFacilityAndName(UUID facilityId, String name) {
        String sql = """
            SELECT EXISTS(
              SELECT 1 FROM court
              WHERE facility_id = :facilityId
                AND lower(name) = lower(:name)
                AND deleted = false
            )
            """;

        Boolean exists = jdbc.queryForObject(sql, Map.of(
                "facilityId", facilityId,
                "name", name
        ), Boolean.class);

        return Boolean.TRUE.equals(exists);
    }
}
