package com.courtbooking.CourtBooking.pricing.domain.model;

import com.courtbooking.CourtBooking.shared.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;
//It defines the structure and attributes of a slot definition within a court booking facility.
// Every slots is linked to a specific facility and court, and it includes details about the time patterns,
// effective dates, status, and associated pricing plans.
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotDefinition extends BaseEntity {

    private UUID slotDefinitionId;
    private UUID facilityId;
    private UUID courtId;

    // ---------- Pattern ----------
    /**
     * Use one of these approaches:
     *  - dayOfWeek: 1=Mon ... 7=Sun (ISO)
     *  - dayType: WEEKDAY / WEEKEND (simpler UI)
     *
     * Keep both nullable; validate "at least one is set".
     */
    private Integer dayOfWeek;     // nullable
    private DayType dayType;       // nullable

    private LocalTime startTime;   // e.g. 18:00
    private LocalTime endTime;     // e.g. 21:00

    // ---------- Effective window (versioning) ----------
    private LocalDate effectiveFrom;        // required
    private LocalDate effectiveTo;          // nullable (open-ended)
    private Boolean active;                 // true if currently usable
    private UUID replacedBySlotDefId;       // optional chain to new version

    // ---------- Metadata ----------
    private String description;

    private UUID pricePlanId;

    public enum DayType {
        WEEKDAY,
        WEEKEND
    }
}
