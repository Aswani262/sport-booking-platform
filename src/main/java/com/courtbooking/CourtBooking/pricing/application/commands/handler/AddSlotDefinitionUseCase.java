package com.courtbooking.CourtBooking.pricing.application.commands.handler;


import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;

import java.util.UUID;

public interface AddSlotDefinitionUseCase {
    UUID handle(AddSlotDefinition command);
}
