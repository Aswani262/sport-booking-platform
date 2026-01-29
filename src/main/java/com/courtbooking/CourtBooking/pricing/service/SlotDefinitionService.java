package com.courtbooking.CourtBooking.pricing.service;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;
import com.courtbooking.CourtBooking.pricing.domain.exceptions.SlotAlreadyExistsException;
import com.courtbooking.CourtBooking.pricing.domain.exceptions.StandardPricingPlanNotFoundException;
import com.courtbooking.CourtBooking.pricing.repository.IPricingPlanRepository;
import com.courtbooking.CourtBooking.pricing.repository.ISlotDefinitionRepository;
import com.courtbooking.CourtBooking.shared.annoation.DomainService;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@DomainService
@RequiredArgsConstructor
public class SlotDefinitionService implements ISlotDefinitionService {

    private final ISlotDefinitionRepository slotRepo;
    private final IPricingPlanRepository pricingRepo;

    @Override
    public void validateSlotDoesNotExist(AddSlotDefinition cmd) {
        boolean exists = slotRepo.existsActiveSamePattern(cmd);
        if (exists) throw new SlotAlreadyExistsException();
    }

    @Override
    public UUID resolvePricePlanId(AddSlotDefinition cmd) {
        if (cmd.getPricePlanId() != null) return cmd.getPricePlanId();

        // Default to standard pricing plan for that court for the effectiveFrom date
        UUID standardId = pricingRepo.findActiveStandardPlanId(cmd.getCourtId(), cmd.getEffectiveFrom());
        if (standardId == null) throw new StandardPricingPlanNotFoundException(cmd.getCourtId());
        return standardId;
    }
}
