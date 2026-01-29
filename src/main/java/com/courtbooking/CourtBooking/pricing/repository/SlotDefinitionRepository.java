package com.courtbooking.CourtBooking.pricing.repository;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;
import com.courtbooking.CourtBooking.pricing.domain.model.SlotDefinition;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;

@Repository
@RequiredArgsConstructor
public class SlotDefinitionRepository implements ISlotDefinitionRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void save(SlotDefinition s) {
        String sql = """
            INSERT INTO slot_definition(
              slot_definition_id, facility_id, court_id,
              day_of_week, day_type,
              start_time, end_time,
              effective_from, effective_to,
              active, replaced_by_slot_def_id,
              description, price_plan_id,
              created_at, updated_at
            )
            VALUES(
              :id, :facilityId, :courtId,
              :dayOfWeek, :dayType,
              :startTime, :endTime,
              :effectiveFrom, :effectiveTo,
              true, null,
              :description, :pricePlanId,
              now(), now()
            )
            """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("id", s.getSlotDefinitionId())
                .addValue("facilityId", s.getFacilityId())
                .addValue("courtId", s.getCourtId())
                .addValue("dayOfWeek", s.getDayOfWeek())
                .addValue("dayType", s.getDayType() == null ? null : s.getDayType().name())
                .addValue("startTime", s.getStartTime())
                .addValue("endTime", s.getEndTime())
                .addValue("effectiveFrom", s.getEffectiveFrom())
                .addValue("effectiveTo", s.getEffectiveTo())
                .addValue("description", s.getDescription())
                .addValue("pricePlanId", s.getPricePlanId());

        jdbc.update(sql, ps);
    }

    @Override
    public boolean existsActiveSamePattern(AddSlotDefinition cmd) {
        // checks active slot with same pattern (court + day pattern + time + effective range overlap)
        String sql = """
            SELECT EXISTS(
              SELECT 1
              FROM slot_definition s
              WHERE s.court_id = :courtId
                AND s.active = true

                -- same day pattern
                AND (
                      ( :dayOfWeek IS NOT NULL AND s.day_of_week = :dayOfWeek )
                   OR ( :dayType IS NOT NULL AND s.day_type = :dayType )
                )

                -- exact time match (you can change to overlap if you want)
                AND s.start_time = :startTime
                AND s.end_time = :endTime

                -- effective date overlap (inclusive)
                AND s.effective_from <= COALESCE(:effectiveTo, DATE '9999-12-31')
                AND COALESCE(s.effective_to, DATE '9999-12-31') >= :effectiveFrom
            )
            """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("courtId", cmd.getCourtId())
                .addValue("dayOfWeek", cmd.getDayOfWeek())
                .addValue("dayType", cmd.getDayType() == null ? null : cmd.getDayType().trim().toUpperCase())
                .addValue("startTime", cmd.getStartTime())
                .addValue("endTime", cmd.getEndTime())
                .addValue("effectiveFrom", cmd.getEffectiveFrom())
                .addValue("effectiveTo", cmd.getEffectiveTo());

        Boolean exists = jdbc.queryForObject(sql, ps, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }
}
