package com.courtbooking.CourtBooking.facility.repository;



import com.courtbooking.CourtBooking.facility.domain.model.Facility;

import java.util.UUID;

public interface IFacilityRepository {
    void save(Facility facility);
    boolean existsActiveByOwnerAndNameAndCity(UUID ownerUserId, String name, String city);
}
