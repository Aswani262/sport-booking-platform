package com.courtbooking.CourtBooking.pricing.application.commands.handler;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;
import com.courtbooking.CourtBooking.pricing.domain.model.SlotDefinition;
import com.courtbooking.CourtBooking.pricing.repository.ISlotDefinitionRepository;
import com.courtbooking.CourtBooking.pricing.service.AddSlotDefinitionValidator;
import com.courtbooking.CourtBooking.pricing.service.ISlotDefinitionService;
import com.courtbooking.CourtBooking.shared.annoation.ApplicationService;
import com.courtbooking.CourtBooking.shared.exception.Notification;
import com.courtbooking.CourtBooking.shared.exception.StructuralValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@ApplicationService
@RequiredArgsConstructor
public class AddSlotDefinitionHandler implements AddSlotDefinitionUseCase {

    private final AddSlotDefinitionValidator validator;
    private final ISlotDefinitionService slotService;
    private final ISlotDefinitionRepository slotRepo;

    @Override
    @Transactional
    public UUID handle(AddSlotDefinition cmd) {

        // 1) structural validation
        Notification n = validator.validate(cmd);
        if (n.hasErrors()) throw new StructuralValidationException(n);

        // 2) business validation
        slotService.validateSlotDoesNotExist(cmd);

        // 3) resolve price plan (default to standard if null)
        UUID pricePlanId = slotService.resolvePricePlanId(cmd);

        // 4) build entity and save
        SlotDefinition slot = SlotDefinition.builder()
                .slotDefinitionId(UUID.randomUUID())
                .facilityId(cmd.getFacilityId())
                .courtId(cmd.getCourtId())
                .dayOfWeek(cmd.getDayOfWeek())
                .dayType(cmd.getDayType() == null ? null : SlotDefinition.DayType.valueOf(cmd.getDayType().trim().toUpperCase()))
                .startTime(cmd.getStartTime())
                .endTime(cmd.getEndTime())
                .effectiveFrom(cmd.getEffectiveFrom())
                .effectiveTo(cmd.getEffectiveTo())
                .active(true)
                .replacedBySlotDefId(null)
                .description(cmd.getDescription())
                .pricePlanId(pricePlanId)
                .build();

        slotRepo.save(slot);
        return slot.getSlotDefinitionId();
    }
}
