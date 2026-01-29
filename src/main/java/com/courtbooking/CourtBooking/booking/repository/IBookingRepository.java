package com.courtbooking.CourtBooking.booking.repository;


import com.courtbooking.CourtBooking.booking.domain.model.Booking;

public interface IBookingRepository {
    void save(Booking booking);
}
