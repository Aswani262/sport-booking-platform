package com.courtbooking.CourtBooking.facility.repository;



import com.courtbooking.CourtBooking.facility.domain.model.Court;

import java.util.UUID;

public interface ICourtRepository {
    void save(Court court);
    boolean existsActiveByFacilityAndName(UUID facilityId, String name);
}
