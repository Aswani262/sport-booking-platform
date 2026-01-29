package com.courtbooking.CourtBooking.booking.repository;


import com.courtbooking.CourtBooking.booking.domain.model.BookingItem;

public interface IBookingItemRepository {
    void insertHeldItem(BookingItem item); // inserts one by one so we know which slot failed
}
