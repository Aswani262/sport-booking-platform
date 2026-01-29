package com.courtbooking.CourtBooking.facility.application.commands.handler;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterFacility;
import com.courtbooking.CourtBooking.facility.domain.model.Facility;
import com.courtbooking.CourtBooking.facility.domain.model.FacilityStatus;
import com.courtbooking.CourtBooking.facility.repository.IFacilityRepository;
import com.courtbooking.CourtBooking.facility.service.IFacilityService;
import com.courtbooking.CourtBooking.facility.service.RegisterFacilityValidator;
import com.courtbooking.CourtBooking.shared.annoation.ApplicationService;

import com.courtbooking.CourtBooking.shared.exception.Notification;
import com.courtbooking.CourtBooking.shared.exception.StructuralValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationService
@RequiredArgsConstructor
public class RegisterFacilityHandler implements RegisterFacilityUseCase {

    private final RegisterFacilityValidator validator; // command validation
    private final IFacilityService facilityService;    // domain rules
    private final IFacilityRepository facilityRepository;

    @Override
    @Transactional
    public UUID handle(RegisterFacility cmd) {

        // 1) Validate command (Notification pattern)
        Notification n = validator.validate(cmd);

        // 2) If any errors => single throw
        if (n.hasErrors()) throw new StructuralValidationException(n);

        // 3) Validate domain rules (existence, etc)
        facilityService.validateFacilityNonExistence(
                cmd.getOwnerUserId(),
                cmd.getName(),
                cmd.getCity()
        );

        // 4) Build entity (simple)
        Facility facility = Facility.builder()
                .facilityId(UUID.randomUUID())
                .ownerUserId(cmd.getOwnerUserId())
                .adminUserId(cmd.getAdminUserId())
                .name(cmd.getName().trim())
                .description(cmd.getDescription())
                .contactPhone(cmd.getContactPhone())
                .contactEmail(cmd.getContactEmail())
                .addressLine1(cmd.getAddressLine1())
                .addressLine2(cmd.getAddressLine2())
                .city(cmd.getCity())
                .state(cmd.getState())
                .country(cmd.getCountry())
                .pincode(cmd.getPincode())
                .latitude(cmd.getLatitude())
                .longitude(cmd.getLongitude())
                .openingTime(cmd.getOpeningTime())
                .closingTime(cmd.getClosingTime())
                .facilityStatus(FacilityStatus.AVAILABLE)
                .deleted(false)
                .build();

        facilityRepository.save(facility);

        return facility.getFacilityId();
    }
}
