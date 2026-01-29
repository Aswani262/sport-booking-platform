package com.courtbooking.CourtBooking.facility.repository;


import com.courtbooking.CourtBooking.facility.domain.model.Facility;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.UUID;

@Repository
@RequiredArgsConstructor
public class FacilityRepository implements IFacilityRepository {

    private final NamedParameterJdbcTemplate jdbc;

    @Override
    public void save(Facility f) {
        String sql = """
            INSERT INTO facility(
              facility_id, owner_user_id, admin_user_id,
              name, description,
              contact_phone, contact_email,
              address_line1, address_line2, city, state, country, pincode,
              latitude, longitude,
              opening_time, closing_time,
              facility_status, deleted,
              created_at, updated_at
            )
            VALUES (
              :facilityId, :ownerUserId, :adminUserId,
              :name, :description,
              :contactPhone, :contactEmail,
              :addressLine1, :addressLine2, :city, :state, :country, :pincode,
              :latitude, :longitude,
              :openingTime, :closingTime,
              :facilityStatus, :deleted,
              now(), now()
            )
            """;

        MapSqlParameterSource p = new MapSqlParameterSource()
                .addValue("facilityId", f.getFacilityId())
                .addValue("ownerUserId", f.getOwnerUserId())
                .addValue("adminUserId", f.getAdminUserId())
                .addValue("name", f.getName())
                .addValue("description", f.getDescription())
                .addValue("contactPhone", f.getContactPhone())
                .addValue("contactEmail", f.getContactEmail())
                .addValue("addressLine1", f.getAddressLine1())
                .addValue("addressLine2", f.getAddressLine2())
                .addValue("city", f.getCity())
                .addValue("state", f.getState())
                .addValue("country", f.getCountry())
                .addValue("pincode", f.getPincode())
                .addValue("latitude", f.getLatitude())
                .addValue("longitude", f.getLongitude())
                .addValue("openingTime", f.getOpeningTime())
                .addValue("closingTime", f.getClosingTime())
                .addValue("facilityStatus", f.getFacilityStatus().name())
                .addValue("deleted", f.getDeleted());

        jdbc.update(sql, p);
    }

    @Override
    public boolean existsActiveByOwnerAndNameAndCity(UUID ownerUserId, String name, String city) {
        String sql = """
            SELECT EXISTS(
              SELECT 1
              FROM facility
              WHERE owner_user_id = :ownerUserId
                AND lower(name) = lower(:name)
                AND lower(city) = lower(:city)
                AND deleted = false
            )
            """;

        Boolean exists = jdbc.queryForObject(sql, Map.of(
                "ownerUserId", ownerUserId,
                "name", name,
                "city", city
        ), Boolean.class);

        return Boolean.TRUE.equals(exists);
    }
}
