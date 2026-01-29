package com.courtbooking.CourtBooking.booking.application.commands.handler;


import com.courtbooking.CourtBooking.booking.application.commands.dtos.BookCourtSlot;

import java.util.UUID;

public interface BookCourtSlotUseCase {
    UUID handle(BookCourtSlot command);
}
