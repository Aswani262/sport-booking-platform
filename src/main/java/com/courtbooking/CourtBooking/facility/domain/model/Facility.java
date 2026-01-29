package com.courtbooking.CourtBooking.facility.domain.model;

import com.courtbooking.CourtBooking.shared.model.BaseEntity;
import lombok.*;

import java.time.LocalTime;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Facility extends BaseEntity {

    private UUID facilityId;
    private UUID ownerUserId;
    private UUID adminUserId;

    private String name;
    private String description;

    private String contactPhone;
    private String contactEmail;

    private String addressLine1;
    private String addressLine2;
    private String city;
    private String state;
    private String country;
    private String pincode;

    //Create index on it
    private Double latitude;
    private Double longitude;

    private LocalTime openingTime;
    private LocalTime closingTime;

    private FacilityStatus facilityStatus;
    private Boolean deleted;
    //Create a index on it
    private String sportTypeSupported;

}
