package com.courtbooking.CourtBooking.facility.api.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class RegisterFacilityResponse {
    private UUID facilityId;
    private String status;
}
