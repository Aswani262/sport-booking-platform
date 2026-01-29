package com.courtbooking.CourtBooking.facility.domain.model;



import com.courtbooking.CourtBooking.shared.model.BaseEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;
import java.util.UUID;
// Represents a specific court within a sports facility
// e.g., Badminton Court 1, Squash Court A
// if facility is closed or not operational, all its courts are unavailable
// Courts can have their own operational hours and booking rules
// Courts can be temporarily marked unavailable for maintenance
// Each court is associated with a specific sport type
// Courts time slots are booked based on facility's operational hours and court's own hours
// Courts have minimum booking duration (e.g., 30 minutes) which affects pricing plans
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Court extends BaseEntity {
    private UUID courtId;
    private UUID facilityId;          // FK -> facility.facility_id

    // What type of court (badminton/squash/etc.)
    private SportType sportType; // or sportId FK if you want normalized

    private String name;              // e.g. "Court 1", "Indoor Court A"
    private String surfaceType;       // optional
    private Integer capacity;         // optional

    // Court-level operational config
    private LocalTime openingTime;    // e.g. 06:00
    private LocalTime closingTime;    // e.g. 23:00
    private Integer minBookingMinutes; // base unit: 30/60 etc.
    private CourtStatus courtStatus;     // AVAILABLE/UNAVAILABLE/UNDER_MAINTENANCE
}
