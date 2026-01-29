package com.courtbooking.CourtBooking.pricing.repository;


import com.courtbooking.CourtBooking.pricing.domain.projection.SlotDefView;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ISlotDefinitionReadRepository {
    List<SlotDefView> findActiveSlotsByIds(UUID courtId, List<UUID> slotDefIds, LocalDate bookingDate);
}
