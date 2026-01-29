package com.courtbooking.CourtBooking.pricing.repository;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;
import com.courtbooking.CourtBooking.pricing.domain.model.SlotDefinition;

public interface ISlotDefinitionRepository {
    void save(SlotDefinition slot);
    boolean existsActiveSamePattern(AddSlotDefinition cmd);
}
