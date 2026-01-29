🏟️ Sports Facility Booking System

Modular Monolith → Microservices Ready Architecture

📌 Overview

This project implements a Sports Facility Booking Platform designed using:

Domain-Driven Design (DDD)

Hexagonal Architecture (Ports & Adapters)

CQRS (Command & Query Separation)

Notification Pattern for validation

Centralized Exception Handling

Database-level concurrency control

Microservices-ready modular monolith design

The system is structured so that each module can be extracted into an independent microservice with minimal refactoring.

🧩 Domain Decomposition (DDD)

The system is decomposed into three subdomains, each mapped to a module (future microservice):

Subdomain	Microservice	Reason
Facility	Facility Service	Inventory management
Pricing & Slots	Pricing Service	High-frequency configuration changes
Booking	Booking Service	High-concurrency transactional core
🏗️ Architecture Style
Hexagonal Architecture (Clean Architecture)

Each service follows:

API Layer (Controllers)
        ↓
Application Layer (UseCases / CommandHandlers / QueryHandlers)
        ↓
Domain Layer (Entities, Domain Services, Business Rules)
        ↓
Adapters (Repositories, Integration Services, Kafka, HTTP)

Custom Stereotype Layers
Annotation	Responsibility
@ApplicationService	Orchestrates use cases, transactions
@DomainService	Business rules & invariants
@IntegrationService	Cross-module/service communication
@UtilityService	Stateless helpers
@EventService	Event publishing/consuming (future Kafka)
🔹 1) Facility Microservice
🎯 Responsibilities

Manage facilities

Manage courts

Geo-based discovery

Sport-based filtering

High-performance search (Elasticsearch)

📦 Core Entities
Facility

facilityId

ownerUserId

name

address

latitude, longitude

sportTypeSupported

openingTime, closingTime

facilityStatus

Court

courtId

facilityId

sport

minBookingMinutes

openingTime, closingTime

courtStatus

🌍 Search Optimization
Indexes Used

GIN index on sportTypeSupported

GiST PostGIS index on geo-location

B-tree on city, status, owner

Elasticsearch Used For

Facility name search

Sport type filtering

Geo-radius search

🌐 Facility APIs
API	Method	Description
/v1/api/facilities	POST	Register facility
/v1/api/facilities/{id}	GET	Get facility details
/v1/api/facilities/{id}/courts	GET	List courts
/v1/api/facilities/nearby	GET	Geo search
/v1/api/facilities/search	GET	Name/sport search
🔹 2) Pricing & Slot Microservice
🎯 Responsibilities

Define pricing rules

Define slot templates

Versioning of slots and pricing

Provide slot availability & pricing

📦 Core Entities
PricingPlan

pricingPlanId

courtId

validFrom, validTo

dayOfWeek, startTime, endTime

pricePerUnit

planType (STANDARD / PEAK)

priority

active

Pricing plans are immutable. Updates create a new version.

SlotDefinition

slotDefinitionId

courtId

dayOfWeek / dayType

startTime, endTime

effectiveFrom, effectiveTo

pricePlanId

active, replacedBySlotDefId

Slots are templates. Updates create new slot versions.

💰 Pricing Types Supported
Type	Behavior
Fixed	Fixed price per slot
Unit-based	Price per minBookingMinutes unit
Peak	Higher priority time window
Weekend	Day-based pricing
🧮 Slot Availability API
API
GET /v1/api/slots/availability?courtId={courtId}&date={yyyy-mm-dd}

Availability Logic

Load SlotDefinitions active for date

Join PricingPlan

Mark unavailable if BookingItem exists with HELD/CONFIRMED

🔹 3) Booking Microservice (Transactional Core)
🎯 Responsibilities

Slot booking (single/multiple)

Payment hold & confirmation

Auto expiry scheduler

Concurrency control

Price snapshot auditing

📦 Core Entities
Booking (Aggregate Root)

bookingId

userId

facilityId

courtId

bookingDate

status

holdExpiresAt

totalAmount

currency

BookingItem (Slot Reservation Row)

bookingItemId

slotDefinitionId

bookingDate

status

priceRuleId

finalPrice

🔄 Booking Flow (End-to-End)
1️⃣ Facility Discovery

User logs in → UI calls Facility Service:

GET /v1/api/facilities/nearby
GET /v1/api/facilities/search

2️⃣ Court Selection
GET /v1/api/facilities/{facilityId}/courts

3️⃣ Slot & Pricing Load
GET /v1/api/slots/availability?courtId={courtId}&date={date}

4️⃣ Booking Slots (HOLD)
POST /v1/api/bookings/book-court-slot

Booking Steps

Structural validation (Notification Pattern)

PricingIntegrationService loads slot pricing

Total price calculated

Booking + BookingItems inserted as HELD

Booking status = PAYMENT_PENDING

holdExpiresAt = now + 10 minutes

5️⃣ Payment Confirmation
POST /v1/api/bookings/{bookingId}/payment/success
POST /v1/api/bookings/{bookingId}/payment/fail

Success

Booking → CONFIRMED

BookingItems → CONFIRMED

Failure

Booking → CANCELLED

BookingItems → CANCELLED

6️⃣ HOLD Expiry Scheduler (Every 1 Minute)

Finds PAYMENT_PENDING bookings where holdExpiresAt < now

Marks Booking → EXPIRED

Marks BookingItems → EXPIRED

This releases slots automatically.

⚔️ Handling Concurrent Booking (CRITICAL)

Concurrency is enforced at database level.

Unique Partial Index (Prevents Double Booking)
CREATE UNIQUE INDEX ux_booking_item_slot_date_active
ON booking_item (court_id, slot_definition_id, booking_date)
WHERE status IN ('HELD','CONFIRMED');

Why This Works

Two users book same slot → one insert fails

Booking service catches exception → returns slot_already_booked

No race condition possible (even with 100 instances)

🧪 Validation Strategy
1) Structural Validation (Notification Pattern)

Missing fields

Invalid formats

Time range invalid

➡ Returns 400 Bad Request

2) Business Validation (Domain Rules)

Examples:

FacilityAlreadyExists

CourtAlreadyExists

SlotAlreadyBooked

➡ Throws typed exceptions → 409 Conflict

🧯 Centralized Exception Handling

All services use @RestControllerAdvice to return consistent error envelope:

{
  "code": "slot_already_booked",
  "message": "Slot already booked for date",
  "timestamp": "...",
  "details": []
}

📡 Service-to-Service Communication
Caller	Calls	Via
Booking → Pricing	Slot pricing	IntegrationService
Booking → Facility	Court validation	IntegrationService
Facility → Search	Elasticsearch	Adapter

In future microservices, IntegrationService becomes REST/Kafka client.

🧠 Key Design Decisions
Immutable Pricing & Slots

Updates create new versions

Old versions retained for audit

Price Snapshot in BookingItem

finalPrice stored permanently

Pricing changes never affect historical bookings

Slot Templates (No Slot Generation)

SlotDefinition defines recurring slots

BookingItem represents actual booked instance

🚀 Scalability Roadmap
Phase 1: Modular Monolith (Current)

Single codebase, separate modules

Shared DB schema

Phase 2: Schema-per-Service

Separate DB per module

Integration via REST

Phase 3: Event Driven

Kafka for:

Facility → Search index update

Court → Facility sport projection update

Booking → Analytics

🗂️ Project Structure
facility/
pricing/
booking/

shared/
  annoation/
  validation/
  exception/
  util/


Each module follows:

api/
application/
domain/
repository/
integration/
util/

🧪 Tech Stack

Java 21

Spring Boot

Spring JDBC

PostgreSQL

Elasticsearch

Kafka (future)

Docker

Flyway / Liquibase

✅ Conclusion

This system is designed for:

High concurrency booking

Frequent pricing changes

Geo-based facility discovery

Microservices-ready extraction

Audit-safe financial correctness

The architecture follows enterprise-grade patterns used by Booking.com, OYO, Uber, and airline reservation systems.

✨ Next Enhancements (Planned)

Redis caching for slot availability

Saga pattern for payment orchestration

Event sourcing for bookings

CQRS read model in Elasticsearch

Kubernetes deployment manifests

Rate limiting & circuit breakers
