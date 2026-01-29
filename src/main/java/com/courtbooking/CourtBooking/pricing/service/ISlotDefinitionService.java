package com.courtbooking.CourtBooking.pricing.service;


import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;

import java.util.UUID;

public interface ISlotDefinitionService {
    UUID resolvePricePlanId(AddSlotDefinition cmd);
    void validateSlotDoesNotExist(AddSlotDefinition cmd);
}
