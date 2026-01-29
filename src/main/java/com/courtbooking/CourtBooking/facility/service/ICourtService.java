package com.courtbooking.CourtBooking.facility.service;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterCourt;

public interface ICourtService {
    void validateBusinessRules(RegisterCourt cmd);
}
