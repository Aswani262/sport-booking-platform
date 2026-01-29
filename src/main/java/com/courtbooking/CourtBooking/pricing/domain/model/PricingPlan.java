package com.courtbooking.CourtBooking.pricing.domain.model;

import com.courtbooking.CourtBooking.shared.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.UUID;
// Pricing plan for court bookings
// User can define multiple pricing plans for a court
// Plans can overlap, in which case the one with higher priority wins
// Plans can be for specific days of week and time ranges
// e.g. "Standard" plan for all days, "Peak Hours" plan for Mon-Fri 18:00-22:00
// Plans have validity periods (validFrom, validTo)
// Plans have price per booking unit (e.g. per 30 minutes)
// if plans priority is same, then the latest created plan wins or you can define other tie-breaker logic
// like which gives higher price



//Every court have at lest on standard pricing plan which is applicable for all days and time

//Its immutable once created , every time a update is needed a new pricing plan is created with new validity dates
// and old one is marked as inactive , which helps in maintaining history of pricing plans
// pricing plans are linked to slot definitions for easy retrieval during booking
// help is linked with booking items for audit purpose and for future booking will not impact
// if pricing plan is changed
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PricingPlan extends BaseEntity {

    private UUID pricingPlanId;
    private UUID facilityId;
    private UUID courtId;

    private String name;         // "Standard", "Premium"

    private String currency;     // "INR"
    private Boolean active;
    private LocalDate validFrom;
    private LocalDate validTo;      // inclusive (you can treat as inclusive)

    // Optional filters
    private Integer dayOfWeek;      // 1=Mon..7=Sun, null => all days
    private LocalTime startTime;    // null => whole day
    private LocalTime endTime;      // null => whole day

    // Price
    private BigDecimal pricePerUnit;   // price per booking unit (minBookingMinutes)
    private PlanType planType ;
    // STANDARD, PEAK_HOURS
    //Standard plan is default plan applicable for all days and time

    private PricePlanPriority priority;         // higher wins if multiple rules match
}
