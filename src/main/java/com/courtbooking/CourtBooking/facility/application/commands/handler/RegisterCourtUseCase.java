package com.courtbooking.CourtBooking.facility.application.commands.handler;



import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterCourt;

import java.util.UUID;

public interface RegisterCourtUseCase {
    UUID handle(RegisterCourt command);
}
