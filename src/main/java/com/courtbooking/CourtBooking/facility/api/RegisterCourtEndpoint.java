package com.courtbooking.CourtBooking.facility.api;

import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterCourt;
import com.courtbooking.CourtBooking.facility.application.commands.handler.RegisterCourtUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/api/courts")
public class RegisterCourtEndpoint {

    private final RegisterCourtUseCase registerCourtUseCase;

    @PostMapping
    public ResponseEntity<Map<String, Object>> register(@RequestBody RegisterCourt command) {
        UUID courtId = registerCourtUseCase.handle(command);
        return ResponseEntity.ok(Map.of("courtId", courtId, "status", "CREATED"));
    }
}
