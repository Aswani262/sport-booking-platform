package com.courtbooking.CourtBooking.facility.service;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterCourt;
import com.courtbooking.CourtBooking.facility.domain.exceptions.CourtAlreadyExistsException;
import com.courtbooking.CourtBooking.facility.repository.ICourtRepository;
import com.courtbooking.CourtBooking.shared.annoation.DomainService;
import lombok.RequiredArgsConstructor;

@DomainService
@RequiredArgsConstructor
public class CourtService implements ICourtService {

    private final ICourtRepository courtRepository;

    @Override
    public void validateBusinessRules(RegisterCourt cmd) {
        // court name must be unique within a facility (active)
        boolean exists = courtRepository.existsActiveByFacilityAndName(
                cmd.getFacilityId(),
                cmd.getName().trim()
        );
        if (exists) throw new CourtAlreadyExistsException(cmd.getName().trim());
    }
}
