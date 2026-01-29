package com.courtbooking.CourtBooking.booking.service;

import com.courtbooking.CourtBooking.booking.application.commands.dtos.BookCourtSlot;
import com.courtbooking.CourtBooking.booking.service.BookingService.BookingComputation;

public interface IBookingService {
    BookingComputation computePricingAndValidate(BookCourtSlot cmd);
}
