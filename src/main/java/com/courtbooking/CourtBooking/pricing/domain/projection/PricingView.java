package com.courtbooking.CourtBooking.pricing.domain.projection;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class PricingView {
    private UUID pricePlanId;
    private BigDecimal pricePerUnit;
    private String currency;
}
