package com.courtbooking.CourtBooking.shared.exception;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(StructuralValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleStructural(StructuralValidationException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(
                ApiErrorResponse.of(
                        ex.getCode(),
                        ex.getMessage(),
                        ex.getNotification().view()
                )
        );
    }

    @ExceptionHandler(BusinessValidationException.class)
    public ResponseEntity<ApiErrorResponse> handleBusiness(BusinessValidationException ex) {
        return ResponseEntity.status(ex.getHttpStatus()).body(
                ApiErrorResponse.of(
                        ex.getCode(),
                        ex.getMessage(),
                        null
                )
        );
    }

    // Optional fallback:
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleUnexpected(Exception ex) {
        return ResponseEntity.internalServerError().body(
                ApiErrorResponse.of(
                        "internal_server_error",
                        "Something went wrong",
                        null
                )
        );
    }
}
