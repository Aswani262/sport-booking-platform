package com.courtbooking.CourtBooking.facility.repository;

import java.util.UUID;

public interface ICourtReadRepository {
    Integer findMinBookingMinutes(UUID courtId);
}
