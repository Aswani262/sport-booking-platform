package com.courtbooking.CourtBooking.pricing.repository;


import com.courtbooking.CourtBooking.pricing.domain.projection.PricingView;

import java.util.UUID;

public interface IPricingReadRepository {
    PricingView findPricing(UUID pricePlanId);
}
