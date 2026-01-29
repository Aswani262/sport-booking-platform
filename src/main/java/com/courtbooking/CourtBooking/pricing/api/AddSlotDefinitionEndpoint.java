package com.courtbooking.CourtBooking.pricing.api;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddSlotDefinition;
import com.courtbooking.CourtBooking.pricing.application.commands.handler.AddSlotDefinitionUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/api/slot-definitions")
public class AddSlotDefinitionEndpoint {

    private final AddSlotDefinitionUseCase useCase;

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody AddSlotDefinition command) {
        UUID id = useCase.handle(command);
        return ResponseEntity.ok(Map.of("slotDefinitionId", id, "status", "CREATED"));
    }
}
