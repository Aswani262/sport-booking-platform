package com.courtbooking.CourtBooking.facility.api;


import com.courtbooking.CourtBooking.facility.api.dtos.RegisterFacilityResponse;

import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterFacility;
import com.courtbooking.CourtBooking.facility.application.commands.handler.RegisterFacilityUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/api/facilities")
public class RegisterFacilityEndpoint {

    private final RegisterFacilityUseCase registerFacilityUseCase;

    @PostMapping
    public ResponseEntity<RegisterFacilityResponse> register(@RequestBody RegisterFacility command) {
        var facilityId = registerFacilityUseCase.handle(command);
        return ResponseEntity.ok(new RegisterFacilityResponse(facilityId, "CREATED"));
    }
}
