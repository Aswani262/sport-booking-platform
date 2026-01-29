package com.courtbooking.CourtBooking.facility.application.commands.handler;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterCourt;
import com.courtbooking.CourtBooking.facility.domain.model.Court;
import com.courtbooking.CourtBooking.facility.domain.model.CourtStatus;
import com.courtbooking.CourtBooking.facility.domain.model.SportType;
import com.courtbooking.CourtBooking.facility.repository.ICourtRepository;
import com.courtbooking.CourtBooking.facility.service.ICourtService;
import com.courtbooking.CourtBooking.facility.service.RegisterCourtValidator;
import com.courtbooking.CourtBooking.shared.annoation.ApplicationService;

import com.courtbooking.CourtBooking.shared.exception.Notification;
import com.courtbooking.CourtBooking.shared.exception.StructuralValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationService
@RequiredArgsConstructor
public class RegisterCourtHandler implements RegisterCourtUseCase {

    private final RegisterCourtValidator validator; // structural validation
    private final ICourtService courtService;        // business validation
    private final ICourtRepository courtRepository;  // persistence

    @Override
    @Transactional
    public UUID handle(RegisterCourt cmd) {

        // 1) Structural validation
        Notification n = validator.validate(cmd);
        if (n.hasErrors()) throw new StructuralValidationException(n);

        // 2) Business validation (throws BusinessValidationException subclasses)
        courtService.validateBusinessRules(cmd);

        // 3) Build & save (simple CRUD entity)
        Court court = Court.builder()
                .courtId(UUID.randomUUID())
                .facilityId(cmd.getFacilityId())
                .sportType(SportType.valueOf(cmd.getSport().trim().toUpperCase())) // adapt if your enum differs
                .name(cmd.getName().trim())
                .surfaceType(cmd.getSurfaceType())
                .capacity(cmd.getCapacity())
                .openingTime(cmd.getOpeningTime())
                .closingTime(cmd.getClosingTime())
                .minBookingMinutes(cmd.getMinBookingMinutes())
                .courtStatus(CourtStatus.AVAILABLE)
                .build();

        courtRepository.save(court);
        return court.getCourtId();
    }
}
