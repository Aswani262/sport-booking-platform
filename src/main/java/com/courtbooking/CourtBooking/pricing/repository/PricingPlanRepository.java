package com.courtbooking.CourtBooking.pricing.repository;

import com.courtbooking.CourtBooking.pricing.domain.model.PricingPlan;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;

import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PricingPlanRepository implements IPricingPlanRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public UUID findActiveStandardPlanId(UUID courtId, LocalDate date) {
        String sql = """
        SELECT p.pricing_plan_id
        FROM pricing_plan p
        WHERE p.court_id = :courtId
          AND p.active = true
          AND p.plan_type = 'STANDARD'
          AND p.day_of_week IS NULL
          AND p.start_time IS NULL
          AND p.end_time IS NULL
          AND p.valid_from <= :date
          AND COALESCE(p.valid_to, DATE '9999-12-31') >= :date
        ORDER BY p.created_at DESC
        LIMIT 1
        """;

        var rows = jdbc.query(sql, Map.of("courtId", courtId, "date", date),
                (rs, i) -> rs.getObject("pricing_plan_id", java.util.UUID.class));

        return rows.isEmpty() ? null : rows.get(0);
    }


    @Override
    public void save(PricingPlan p) {
        String sql = """
            INSERT INTO pricing_plan(
              pricing_plan_id, facility_id, court_id,
              name, currency, active,
              valid_from, valid_to,
              day_of_week, start_time, end_time,
              price_per_unit, plan_type, priority,
              created_at, updated_at
            )
            VALUES(
              :id, :facilityId, :courtId,
              :name, :currency, true,
              :validFrom, :validTo,
              :dayOfWeek, :startTime, :endTime,
              :pricePerUnit, :planType, :priority,
              now(), now()
            )
            """;

        MapSqlParameterSource ps = new MapSqlParameterSource()
                .addValue("id", p.getPricingPlanId())
                .addValue("facilityId", p.getFacilityId())
                .addValue("courtId", p.getCourtId())
                .addValue("name", p.getName())
                .addValue("currency", p.getCurrency())
                .addValue("validFrom", p.getValidFrom())
                .addValue("validTo", p.getValidTo())
                .addValue("dayOfWeek", p.getDayOfWeek())
                .addValue("startTime", p.getStartTime())
                .addValue("endTime", p.getEndTime())
                .addValue("pricePerUnit", p.getPricePerUnit())
                .addValue("planType", p.getPlanType().name())
                .addValue("priority", p.getPriority()); // implement asInt()

        jdbc.update(sql, ps);
    }

    /**
     * Overlap logic:
     * - Only checks active plans for same court
     * - Date ranges overlap (inclusive)
     * - day_of_week matches OR either side is null(all days)
     * - time windows overlap OR either side is null(whole day)
     * - existing plan priority >= new plan priority
     */
    @Override
    public boolean overlapsWithActivePlansSameOrHigherPriority(AddPricingPlan cmd) {

        String sql = """
            SELECT EXISTS(
              SELECT 1
              FROM pricing_plan p
              WHERE p.court_id = :courtId
                AND p.active = true

                -- date overlap (inclusive). treat null valid_to as open-ended
                AND p.valid_from <= COALESCE(:validTo, DATE '9999-12-31')
                AND COALESCE(p.valid_to, DATE '9999-12-31') >= :validFrom

                -- day match
                AND (
                     p.day_of_week IS NULL
                  OR :dayOfWeek IS NULL
                  OR p.day_of_week = :dayOfWeek
                )

                -- time overlap (if both windows defined). If any is whole day -> overlap
                AND (
                     p.start_time IS NULL OR p.end_time IS NULL
                  OR :startTime IS NULL OR :endTime IS NULL
                  OR (p.start_time < :endTime AND p.end_time > :startTime)
                )

                -- same or higher priority blocks creation (your policy)
                AND p.priority >= :priority
            )
            """;

        Map<String, Object> params = Map.of(
                "courtId", cmd.getCourtId(),
                "validFrom", cmd.getValidFrom(),
                "validTo", cmd.getValidTo(),
                "dayOfWeek", cmd.getDayOfWeek(),
                "startTime", cmd.getStartTime(),
                "endTime", cmd.getEndTime(),
                "priority", cmd.getPriority()
        );

        Boolean exists = jdbc.queryForObject(sql, params, Boolean.class);
        return Boolean.TRUE.equals(exists);
    }

    @Override
    public boolean existsActiveStandardWholeDayAllDays(UUID courtId, LocalDate date) {
        String sql = """
            SELECT EXISTS(
              SELECT 1
              FROM pricing_plan p
              WHERE p.court_id = :courtId
                AND p.active = true
                AND p.plan_type = 'STANDARD'
                AND p.day_of_week IS NULL
                AND p.start_time IS NULL
                AND p.end_time IS NULL
                AND p.valid_from <= :date
                AND COALESCE(p.valid_to, DATE '9999-12-31') >= :date
            )
            """;
        Boolean exists = jdbc.queryForObject(sql, Map.of("courtId", courtId, "date", date), Boolean.class);
        return Boolean.TRUE.equals(exists);
    }
}
