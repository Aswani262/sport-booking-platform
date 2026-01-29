package com.courtbooking.CourtBooking.facility.service;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterFacility;
import com.courtbooking.CourtBooking.facility.domain.exceptions.FacilityAlreadyExistsException;
import com.courtbooking.CourtBooking.facility.repository.IFacilityRepository;

import com.courtbooking.CourtBooking.shared.annoation.DomainService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class FacilityService implements IFacilityService {

    private final IFacilityRepository facilityRepository;

    @Override
    public void validateBusinessRules(RegisterFacility cmd) {
        if (cmd.getOwnerUserId() == null || cmd.getName() == null || cmd.getCity() == null) return;

        boolean exists = facilityRepository.existsActiveByOwnerAndNameAndCity(
                cmd.getOwnerUserId(),
                cmd.getName().trim(),
                cmd.getCity().trim()
        );

        if (exists) {
            throw new FacilityAlreadyExistsException(cmd.getName().trim(), cmd.getCity().trim());
        }
    }
}
