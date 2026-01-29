package com.courtbooking.CourtBooking.pricing.api;

import com.courtbooking.CourtBooking.pricing.application.commands.dtos.AddPricingPlan;
import com.courtbooking.CourtBooking.pricing.application.commands.handler.AddPricingPlanUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/api/pricing-plans")
public class AddPricingPlanEndpoint {

    private final AddPricingPlanUseCase useCase;

    @PostMapping
    public ResponseEntity<Map<String, Object>> add(@RequestBody AddPricingPlan command) {
        UUID id = useCase.handle(command);
        return ResponseEntity.ok(Map.of("pricingPlanId", id, "status", "CREATED"));
    }
}
