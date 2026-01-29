package com.courtbooking.CourtBooking.booking.api;

import com.courtbooking.CourtBooking.booking.application.commands.dtos.BookCourtSlot;
import com.courtbooking.CourtBooking.booking.application.commands.handler.BookCourtSlotUseCase;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/api/bookings")
public class BookingEndpoint {

    private final BookCourtSlotUseCase useCase;

    @PostMapping("/book-court-slot")
    public ResponseEntity<Map<String, Object>> bookCourtSlot(@RequestBody BookCourtSlot command) {
        UUID bookingId = useCase.handle(command);
        return ResponseEntity.ok(Map.of("bookingId", bookingId, "status", "PAYMENT_PENDING"));
    }
}
