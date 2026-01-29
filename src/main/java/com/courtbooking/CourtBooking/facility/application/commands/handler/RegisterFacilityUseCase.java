package com.courtbooking.CourtBooking.facility.application.commands.handler;

import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterFacility;

import java.util.UUID;

public interface RegisterFacilityUseCase {
    UUID handle(RegisterFacility command);
}
