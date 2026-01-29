package com.courtbooking.CourtBooking.shared.model;

import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class BaseEntity {

    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
    private UUID createdBy;          // FK -> users.user_id
    private UUID updatedBy;
}
