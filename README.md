1) Booking Microservice
1.1 Responsibilities

The Booking service is the transactional and concurrency-critical service. It owns:

Creating a booking (single/multiple slots)

Reserving slots with HOLD

Confirming booking after payment

Expiring unpaid holds automatically (scheduler)

Ensuring no double booking under high concurrency

Storing audit-safe price snapshots per booked slot (priceRuleId, finalPrice)

Core Entities (from your model)

Booking (aggregate root): bookingId, userId, facilityId, courtId, bookingDate, status, holdExpiresAt, totalAmount, currency

BookingItem (reservation rows): bookingId, courtId, slotDefinitionId, bookingDate, status, priceRuleId, finalPrice

1.2 Booking Flow (End-to-End)
Step A — User discovers facilities (search)

User logs in

UI calls Facility service to fetch nearest facilities based on user location

Facility service uses Elasticsearch for fast geo + text queries

Facility Service APIs used:

GET /v1/api/facilities/nearby?lat={lat}&lon={lon}&radiusKm={km}&sportType={sport}

GET /v1/api/facilities/search?q={text}&sportType={sport}

Step B — User selects facility & court

Once user selects a facility:

UI fetches courts of that facility

Facility Service APIs used:

GET /v1/api/facilities/{facilityId}

GET /v1/api/facilities/{facilityId}/courts

Step C — UI loads available slots + pricing for a court on a date

Availability is computed from:

SlotDefinition (template slots)

PricingPlan (pricing linked to each slot definition)

BookingItem (already HELD/CONFIRMED reservations)

Pricing & Slots Service APIs used:

GET /v1/api/slots/availability?courtId={courtId}&date={yyyy-mm-dd}

returns list of slot DTOs with pricing and availability status

How availability is derived:

Fetch all active SlotDefinitions effective for that date

Mark a slot as unavailable if a booking_item exists for (court_id, slot_definition_id, booking_date) with status in HELD/CONFIRMED

Availability query logic (conceptual):

Available = slot_definition exists AND no booking_item row exists for that slot/date in HELD/CONFIRMED

Step D — User selects one/multiple slots and books

UI sends slot ids to Booking service.

Booking API:

POST /v1/api/bookings/book-court-slot

Request:

{
  "userId": "...",
  "facilityId": "...",
  "courtId": "...",
  "bookingDate": "2026-02-01",
  "slotDefinitionIds": ["...", "..."],
  "holdMinutes": 10,
  "currency": "INR"
}


What Booking service does:

Validate request using Notification Pattern (structural validation)

Calls Pricing service (integration) to load pricing for selected slots

Calculates total and generates Booking + BookingItems

Inserts BookingItems as HELD

Booking status becomes PAYMENT_PENDING

Returns bookingId to UI

Step E — Payment flow

After booking is placed on HOLD:

UI redirects user to payment

Payment provider returns success/fail to booking service

Booking APIs:

POST /v1/api/bookings/{bookingId}/payment/success

POST /v1/api/bookings/{bookingId}/payment/fail

On success:

Booking: PAYMENT_PENDING → CONFIRMED

BookingItem: HELD → CONFIRMED

On failure:

Booking: PAYMENT_PENDING → CANCELLED

BookingItem: HELD → CANCELLED

1.3 HOLD Expiry Scheduler (runs every 1 minute)

A scheduler runs every minute:

Finds PAYMENT_PENDING bookings where hold_expires_at < now()

Marks booking as EXPIRED

Marks all items HELD → EXPIRED
This releases the slot for future bookings.

Booking Scheduler job:

every 1 minute

action: expire holds

1.4 Handling Concurrent Booking (CRITICAL)
How concurrency is guaranteed

Concurrency is enforced at database level, not application logic.

We prevent double booking using a unique partial index on booking_item:

✅ Index you asked to add:

CREATE UNIQUE INDEX ux_booking_item_slot_date_active
ON booking_item (court_id, slot_definition_id, booking_date)
WHERE status IN ('HELD','CONFIRMED');

Why this works

If two users try to reserve same slot at same time:

one insert succeeds

second insert fails with DuplicateKeyException

Booking service catches it and returns business error:

slot_already_booked (HTTP 409)

This guarantees correctness even under:

high traffic

parallel requests

retries

multiple app instances

2) Pricing & Slot Microservice
2.1 Responsibilities

This service manages all time and pricing rules for courts, optimized for frequent admin updates.

It owns:

Pricing Plan rules (PricingPlan)

Slot Definitions (SlotDefinition)

Slot availability query (derived with booking reservations)

Versioning strategy: immutable pricing plans and slot definitions (new version instead of updates)

Core Entities (from your model)
PricingPlan

Key fields:

pricingPlanId, facilityId, courtId, name, currency, active

validFrom, validTo

optional filters: dayOfWeek, startTime, endTime

price: pricePerUnit

planType (STANDARD/PEAK etc.)

priority (higher wins when overlaps)

SlotDefinition

Key fields:

slotDefinitionId, facilityId, courtId

Pattern: dayOfWeek OR dayType (WEEKDAY/WEEKEND)

Time range: startTime, endTime

Effective window: effectiveFrom, effectiveTo, active, replacedBySlotDefId

Linked pricing: pricePlanId

2.2 Pricing Management Flow (Admin)
Step 1 — Define Pricing Plan(s)

Admin creates pricing rules for a court.

Pricing APIs:

POST /v1/api/pricing-plans

PUT /v1/api/pricing-plans/{pricingPlanId}/deactivate

GET /v1/api/pricing-plans?courtId={courtId}

Rules supported:

Standard pricing plan (applies all day)

Peak hour pricing (Mon–Fri 18:00–22:00)

Weekend pricing

Date-range pricing (festival weeks)

Overlaps are resolved by:

priority (higher wins)

tie-breaker: latest created wins (optional policy)

PricingPlan is immutable: updates create new plan and deactivate old.

2.3 Slot Definition Flow (Admin)
Step 2 — Define Slots (templates) for the court

Admin defines slots using SlotDefinition templates.

Slot APIs:

POST /v1/api/slot-definitions

PUT /v1/api/slot-definitions/{slotDefinitionId}/deactivate

GET /v1/api/slot-definitions?courtId={courtId}

Each slot definition must:

define a time range

define a pattern (dayOfWeek OR dayType)

have effectiveFrom/effectiveTo

attach a pricePlanId

Price attachment rule

If pricePlanId not provided in slot definition:

default to STANDARD plan (for that court and effective date)

2.4 Supported Pricing Types (Fixed + Unit Based)

You mentioned 3 types including fixed and unit-based.

Your current entity supports:

Unit-based naturally: pricePerUnit multiplied by slotDuration / minBookingMinutes

To support fixed explicitly, best practice is to add:

pricingMode = FIXED | UNIT

if FIXED: fixedPrice

if UNIT: pricePerUnit

(You can keep backward compatibility by treating FIXED as “unit=1”.)

2.5 Availability API (used by UI)

This service provides the slot list for a given court/date with availability:

API:

GET /v1/api/slots/availability?courtId={courtId}&date={yyyy-mm-dd}

Response includes:

slotDefinitionId

start/end time

pricingPlanId

computed price (for that slot)

available = true/false (derived from Booking reservations)

3) Facility Microservice
3.1 Responsibilities

Facility service is the “inventory” service for:

Facility onboarding & management

Court management under facility

Fast facility discovery (geo + text + sport filters)

Core Entities

Facility: address, geo, opening/closing, status, sportTypeSupported

Court: sport, minBookingMinutes, timings, status

3.2 Capabilities

Facility APIs:

POST /v1/api/facilities (register facility)

PUT /v1/api/facilities/{facilityId} (update)

DELETE /v1/api/facilities/{facilityId} (soft delete)

GET /v1/api/facilities/{facilityId}

Court APIs:

POST /v1/api/courts (register court)

PUT /v1/api/courts/{courtId}

DELETE /v1/api/courts/{courtId} (soft delete)

GET /v1/api/facilities/{facilityId}/courts

3.3 Fast Querying (Geo + sport type + name)

You mentioned:

index on lat/lon

index on sportTypeSupported

court info indexed

elasticsearch for optimized query

Recommended query design

Elasticsearch for:

facility name search

sport type filter

geo radius search

Postgres indexes for:

admin CRUD

internal joins and consistency

Useful DB indexes

city, status, owner

sportTypeSupported (GIN if array)

geo: PostGIS GiST index (best)

4) Cross-Service Interactions
Booking → PricingSlots

Booking needs slot pricing and slot validity.

Done via PricingIntegrationService calling pricing application query handler.

Booking → Facility

Booking validates court/facility exist (optional).

Typically via FacilityIntegrationService or cached read model.

5) Patterns Used Across All Services
Notification Pattern (Structural Validation)

Collect all validation errors

Return 400 with a list of issues

Example codes: name_required, time_range_invalid

Business Validation (Domain Rules)

Throw typed exceptions like:

SlotAlreadyBookedException

CourtAlreadyExistsException

Returned as 409/422 depending on rule

Centralized Exception Handling

Standard API error envelope

Same structure across all services

Query vs Command Handlers (CQRS-style)

Command handlers: mutate state

Query handlers: optimized read DTOs
