package com.courtbooking.CourtBooking.facility.service;


import com.courtbooking.CourtBooking.facility.application.commands.dtos.RegisterFacility;

public interface IFacilityService {
    void validateBusinessRules(RegisterFacility cmd);
}
