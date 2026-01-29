package com.courtbooking.CourtBooking.pricing.repository;

import com.courtbooking.CourtBooking.pricing.domain.projection.PricingView;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class PricingReadRepository implements IPricingReadRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public PricingView findPricing(UUID pricePlanId) {
        String sql = """
            SELECT pricing_plan_id, price_per_unit, currency
            FROM pricing_plan
            WHERE pricing_plan_id = :id AND active = true
            """;
        List<PricingView> rows = jdbc.query(sql, Map.of("id", pricePlanId),
                (rs, i) -> new PricingView(
                        rs.getObject("pricing_plan_id", UUID.class),
                        rs.getBigDecimal("price_per_unit"),
                        rs.getString("currency")
                ));
        return rows.isEmpty() ? null : rows.get(0);
    }
}
