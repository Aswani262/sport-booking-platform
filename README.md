# Sports Facility Booking System


---

## Overview

This project implements a Sports Facility Booking Platform designed using:

- Domain-Driven Design (DDD)
- Hexagonal Architecture (Ports & Adapters)
- CQRS (Command & Query Separation)
- Notification Pattern for validation
- Centralized Exception Handling
- Database-level concurrency control
- Microservices-ready modular monolith design

The system is structured so that each module can be extracted into an independent microservice with minimal refactoring.

---

## Domain Decomposition (DDD)

The system is divided into three subdomains:

1. Facility Microservice  
2. Pricing & Slot Microservice  
3. Booking Microservice  

Each subdomain represents a bounded context and can be deployed independently.

---

# Facility Microservice

## Responsibilities

- Manage sports facilities
- Manage courts inside a facility
- Geo-based facility search
- Sport-based filtering
- Elasticsearch-based optimized search

## Core Entities

### Facility
- facilityId
- ownerUserId
- name
- description
- address
- latitude
- longitude
- sportTypeSupported
- openingTime
- closingTime
- facilityStatus

### Court
- courtId
- facilityId
- sport
- minBookingMinutes
- openingTime
- closingTime
- courtStatus

## APIs

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | /v1/api/facilities | Register facility |
| GET | /v1/api/facilities/{id} | Get facility details |
| GET | /v1/api/facilities/{id}/courts | List courts |
| GET | /v1/api/facilities/nearby | Search nearby facilities |
| GET | /v1/api/facilities/search | Search by name or sport |

---

# Pricing and Slot Microservice

## Responsibilities

- Define pricing rules
- Define slot templates
- Pricing versioning
- Slot versioning
- Provide slot availability with pricing

## Core Entities

### PricingPlan
- pricingPlanId
- courtId
- validFrom
- validTo
- dayOfWeek
- startTime
- endTime
- pricePerUnit
- planType (STANDARD, PEAK)
- priority
- active

Pricing plans are immutable. Updates create a new version.

### SlotDefinition
- slotDefinitionId
- courtId
- dayOfWeek or dayType (WEEKDAY, WEEKEND)
- startTime
- endTime
- effectiveFrom
- effectiveTo
- pricePlanId
- active
- replacedBySlotDefId

Slot definitions are templates. Updates create new versions.

## Pricing Types

- Fixed Price
- Unit-based Price (per min booking unit)
- Peak Hour Pricing
- Weekend Pricing

## Slot Availability API

GET /v1/api/slots/availability?courtId={courtId}&date={yyyy-mm-dd}

Availability is calculated using SlotDefinition + PricingPlan + Booking reservations.

---

# Booking Microservice

## Responsibilities

- Book one or multiple slots
- Payment hold and confirmation
- Slot expiration scheduler
- Concurrency control
- Price snapshot auditing

## Core Entities

### Booking (Aggregate Root)
- bookingId
- userId
- facilityId
- courtId
- bookingDate
- status
- holdExpiresAt
- totalAmount
- currency

### BookingItem
- bookingItemId
- slotDefinitionId
- bookingDate
- status
- priceRuleId
- finalPrice

---

## Booking Flow

### 1. Discover Facility
GET /v1/api/facilities/nearby  
GET /v1/api/facilities/search  

### 2. Select Court
GET /v1/api/facilities/{facilityId}/courts  

### 3. Load Slots and Pricing
GET /v1/api/slots/availability?courtId={courtId}&date={date}  

### 4. Book Slots (HOLD)
POST /v1/api/bookings/book-court-slot  

Booking steps:
1. Validate request using Notification Pattern
2. Load pricing from Pricing Service
3. Compute total price
4. Insert Booking and BookingItems as HELD
5. Booking status = PAYMENT_PENDING
6. holdExpiresAt = now + 10 minutes

### 5. Payment APIs
POST /v1/api/bookings/{bookingId}/payment/success  
POST /v1/api/bookings/{bookingId}/payment/fail  

### 6. HOLD Expiry Scheduler (Every 1 Minute)

- Finds PAYMENT_PENDING bookings where holdExpiresAt < now
- Marks Booking as EXPIRED
- Marks BookingItems as EXPIRED

---

# Concurrency Handling

Concurrency is enforced at database level.

## Unique Partial Index

```sql
CREATE UNIQUE INDEX ux_booking_item_slot_date_active
ON booking_item (court_id, slot_definition_id, booking_date)
WHERE status IN ('HELD','CONFIRMED');


## Validation Strategy
   Structural Validation

   Implemented using Notification Pattern

   Returns HTTP 400

   Business Validation

   FacilityAlreadyExists

   CourtAlreadyExists

   SlotAlreadyBooked

   Returns HTTP 409
