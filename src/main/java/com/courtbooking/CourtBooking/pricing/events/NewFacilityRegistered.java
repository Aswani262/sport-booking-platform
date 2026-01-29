package com.courtbooking.CourtBooking.pricing.events;

import lombok.Data;

@Data
public class NewFacilityRegistered extends Facility {
    private String facilityId;
    private String facilityName;
}
