CREATE TABLE facility (
  facility_id uuid PRIMARY KEY,
  owner_user_id uuid NOT NULL,
  admin_user_id uuid NULL,

  name varchar(150) NOT NULL,
  description text NULL,

  contact_phone varchar(20) NULL,
  contact_email varchar(120) NULL,

  address_line1 varchar(255) NOT NULL,
  address_line2 varchar(255) NULL,
  city varchar(100) NOT NULL,
  state varchar(100) NOT NULL,
  country varchar(100) NOT NULL,
  pincode varchar(20) NOT NULL,

  latitude double precision NOT NULL,
  longitude double precision NOT NULL,

  opening_time time NOT NULL,
  closing_time time NOT NULL,

  facility_status varchar(30) NOT NULL,
  deleted boolean NOT NULL DEFAULT false,

  timezone varchar(50) NOT NULL DEFAULT 'UTC',
  sport_type_supported varchar(100) NOT NULL,

  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX ix_facility_sport_type
ON facility (sport_type_supported);

CREATE INDEX ix_facility_lat_lon
ON facility (latitude, longitude);

CREATE TABLE court (
  court_id uuid PRIMARY KEY,
  facility_id uuid NOT NULL REFERENCES facility(facility_id),

  sport varchar(50) NOT NULL,
  name varchar(120) NOT NULL,
  surface_type varchar(80),
  capacity int,

  opening_time time NOT NULL,
  closing_time time NOT NULL,
  min_booking_minutes int NOT NULL,

  court_status varchar(30) NOT NULL,
  deleted boolean NOT NULL DEFAULT false,

  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);


CREATE TABLE pricing_plan (
  pricing_plan_id uuid PRIMARY KEY,
  facility_id uuid NOT NULL,
  court_id uuid NOT NULL REFERENCES court(court_id),

  name varchar(120) NOT NULL,
  currency varchar(3) NOT NULL,
  active boolean NOT NULL DEFAULT true,

  valid_from date NOT NULL,
  valid_to date NULL,

  day_of_week int NULL,
  start_time time NULL,
  end_time time NULL,

  price_per_unit numeric(12,2) NOT NULL,
  plan_type varchar(30) NOT NULL, -- STANDARD, PEAK
  priority int NOT NULL,

  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX ix_pricing_plan_court_active
ON pricing_plan (court_id, active);

CREATE INDEX ix_pricing_plan_valid_range
ON pricing_plan (court_id, valid_from, valid_to);

-- Fast standard plan lookup
CREATE INDEX ix_pricing_plan_standard
ON pricing_plan (court_id)
WHERE plan_type = 'STANDARD' AND active = true;


CREATE TABLE slot_definition (
  slot_definition_id uuid PRIMARY KEY,
  facility_id uuid NOT NULL,
  court_id uuid NOT NULL REFERENCES court(court_id),

  day_of_week int NULL,
  day_type varchar(20) NULL,

  start_time time NOT NULL,
  end_time time NOT NULL,

  effective_from date NOT NULL,
  effective_to date NULL,

  active boolean NOT NULL DEFAULT true,
  replaced_by_slot_def_id uuid NULL,

  description text NULL,
  price_plan_id uuid NOT NULL REFERENCES pricing_plan(pricing_plan_id),

  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);


CREATE TABLE booking (
  booking_id uuid PRIMARY KEY,

  user_id uuid NOT NULL,
  facility_id uuid NOT NULL,
  court_id uuid NOT NULL,

  booking_date timestamptz NOT NULL,
  status varchar(30) NOT NULL,

  hold_expires_at timestamptz NULL,
  booking_type varchar(30) NOT NULL,

  total_amount numeric(12,2) NOT NULL,
  currency varchar(3) NOT NULL,
  payment_transaction_id varchar(120) NULL,

  created_at timestamptz NOT NULL DEFAULT now(),
  updated_at timestamptz NOT NULL DEFAULT now()
);

CREATE INDEX ix_booking_user_date ON booking (user_id, booking_date DESC);
CREATE INDEX ix_booking_facility_date ON booking (facility_id, booking_date DESC);
CREATE INDEX ix_booking_court_date ON booking (court_id, booking_date DESC);
CREATE INDEX ix_booking_status ON booking (status);

-- Expiry job index
CREATE INDEX ix_booking_hold_expires_at
ON booking (hold_expires_at)
WHERE status = 'PAYMENT_PENDING';

CREATE TABLE booking_item (
  booking_item_id uuid PRIMARY KEY,

  booking_id uuid NOT NULL REFERENCES booking(booking_id) ON DELETE CASCADE,
  court_id uuid NOT NULL,
  slot_definition_id uuid NOT NULL REFERENCES slot_definition(slot_definition_id),
  booking_date date NOT NULL,

  status varchar(30) NOT NULL, -- HELD, CONFIRMED, CANCELLED, EXPIRED

  price_rule_id uuid NOT NULL,
  final_price numeric(12,2) NOT NULL,

  created_at timestamptz NOT NULL DEFAULT now()
);


-- Prevent double booking of same slot on same date
CREATE UNIQUE INDEX ux_booking_item_slot_date_active
ON booking_item (court_id, slot_definition_id, booking_date)
WHERE status IN ('HELD','CONFIRMED');

CREATE INDEX ix_booking_item_booking ON booking_item (booking_id);
CREATE INDEX ix_booking_item_court_date ON booking_item (court_id, booking_date);
CREATE INDEX ix_booking_item_slot_date ON booking_item (slot_definition_id, booking_date);
CREATE INDEX ix_booking_item_status ON booking_item (status);




SELECT s.slot_definition_id
FROM slot_definition s
LEFT JOIN booking_item b
  ON s.slot_definition_id = b.slot_definition_id
 AND b.booking_date = '2026-02-01'
 AND b.status IN ('HELD','CONFIRMED')
WHERE s.court_id = 'court-id'
  AND s.active = true
  AND b.booking_item_id IS NULL;
