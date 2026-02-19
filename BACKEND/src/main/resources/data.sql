-- =========================
-- ADDITIONAL SERVICES (unchanged)
-- =========================
INSERT INTO additional_service (id, name, description) VALUES
(nextval('additional_service_seq'), 'Pet friendly',         'Pets are allowed during the ride as long as they are properly secured.'),
(nextval('additional_service_seq'), 'Baby seat',            'Vehicle is equipped with a certified child safety seat.'),
(nextval('additional_service_seq'), 'Smoking allowed',      'Smoking is permitted inside the vehicle during the ride.'),
(nextval('additional_service_seq'), 'Wheelchair accessible','Vehicle supports wheelchair access with ramp or lift.'),
(nextval('additional_service_seq'), 'WiFi',                 'Free onboard WiFi available for passengers.');

-- =========================
-- VEHICLE TYPES (updated prices: 30–60 based on luxury)
-- =========================
INSERT INTO vehicle_type (id, type_name, description, price) VALUES
(nextval('vehicle_type_seq'), 'Standard', 'Standard passenger vehicle for everyday rides.',                          30.00),
(nextval('vehicle_type_seq'), 'Comfort',  'More spacious and comfortable vehicle with extra legroom.',               40.00),
(nextval('vehicle_type_seq'), 'Electric', 'Eco-friendly electric vehicle with zero emissions.',                      45.00),
(nextval('vehicle_type_seq'), 'Van',      'Larger vehicle suitable for groups or extra luggage.',                    50.00),
(nextval('vehicle_type_seq'), 'Premium',  'High-end vehicle with luxury interior and superior comfort.',             60.00);

-- =========================
-- BASE TEST USERS  (kept exactly as before; passwords = "test123" bcrypt)
-- =========================
INSERT INTO app_user (
    id, role, first_name, last_name, email, password,
    address, phone_number, img_src,
    token, token_expiration,
    is_active, is_blocked, block_reason, driver_status
) VALUES
-- ADMIN
(nextval('app_user_seq'), 'ADMIN', 'Admin', 'User', 'admin@test.com',
 '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
 'Admin Street 1', '+38160000001', NULL, NULL, NULL, true, false, NULL, NULL),

-- CUSTOMER
(nextval('app_user_seq'), 'CUSTOMER', 'John', 'Doe', 'customer@test.com',
 '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
 'Customer Street 5', '+38160000002', NULL, NULL, NULL, true, false, NULL, NULL),

-- DRIVER
(nextval('app_user_seq'), 'DRIVER', 'Mike', 'Driver', 'driver@test.com',
 '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
 'Driver Street 10', '+38160000003', NULL, NULL, NULL, true, false, NULL, 'ACTIVE');

-- Vehicle for the base test driver (Standard)
INSERT INTO vehicle (id, model, license_plate, number_of_seats, driver_id, vehicle_type_id)
VALUES (
    nextval('vehicle_seq'),
    'Toyota Prius',
    'NS-123-AB',
    4,
    (SELECT id FROM app_user WHERE email = 'driver@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard')
);

INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT (SELECT id FROM vehicle WHERE license_plate = 'NS-123-AB'), id
FROM additional_service WHERE name IN ('WiFi', 'Pet friendly', 'Baby seat');

-- =========================
-- GENERATED ADMINS (10 extra)
-- =========================
INSERT INTO app_user (id, role, first_name, last_name, email, password, address, phone_number, is_active, is_blocked)
SELECT
    nextval('app_user_seq'),
    'ADMIN',
    'Admin' || i,  'User',
    'admin' || i || '@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Admin Street ' || i,
    '+38160001' || LPAD(i::text, 2, '0'),
    true, false
FROM generate_series(1, 10) i;

-- =========================
-- GENERATED CUSTOMERS (10 extra; customer3 & customer7 will be blocked later)
-- =========================
INSERT INTO app_user (id, role, first_name, last_name, email, password, address, phone_number, is_active, is_blocked)
SELECT
    nextval('app_user_seq'),
    'CUSTOMER',
    'Customer' || i, 'User',
    'customer' || i || '@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Customer Street ' || i,
    '+38160002' || LPAD(i::text, 2, '0'),
    true, false
FROM generate_series(1, 10) i;

-- =========================
-- GENERATED DRIVERS (10 extra)
-- Assigned vehicle types in round-robin (Standard→Comfort→Electric→Van→Premium):
--   driver10(rn=1)→Standard(30), driver9(rn=2)→Comfort(40), driver8(rn=3)→Electric(45),
--   driver7(rn=4)→Van(50),       driver6(rn=5)→Premium(60), driver5(rn=6)→Standard(30),
--   driver4(rn=7)→Comfort(40),   driver3(rn=8)→Electric(45),driver2(rn=9)→Van(50),
--   driver1(rn=10)→Premium(60)
-- =========================
INSERT INTO app_user (id, role, first_name, last_name, email, password, address, phone_number, is_active, is_blocked, driver_status)
SELECT
    nextval('app_user_seq'),
    'DRIVER',
    'Driver' || i, 'User',
    'driver' || i || '@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Driver Street ' || i,
    '+38160003' || LPAD(i::text, 2, '0'),
    true, false,
    CASE
        WHEN i IN (2, 9)  THEN 'INACTIVE'
        WHEN i = 10       THEN 'WAITING_ACTIVATION'
        ELSE 'ACTIVE'
    END
FROM generate_series(1, 10) i;

-- Vehicles for the 10 generated drivers (round-robin vehicle type)
-- driver10→rn=1→Standard(30), driver9→Comfort(40), driver8→Electric(45),
-- driver7→Van(50), driver6→Premium(60), driver5→Standard(30), driver4→Comfort(40),
-- driver3→Electric(45), driver2→Van(50), driver1→Premium(60)
INSERT INTO vehicle (id, model, license_plate, number_of_seats, driver_id, vehicle_type_id)
SELECT
    nextval('vehicle_seq'),
    'Vehicle Model ' || u.rn,
    'NS-' || (200 + u.rn) || '-DR',
    CASE WHEN vt.type_name = 'Van' THEN 7 ELSE 4 END,
    u.id,
    vt.id
FROM (
    SELECT id, ROW_NUMBER() OVER () AS rn
    FROM app_user
    WHERE role = 'DRIVER'
    ORDER BY id DESC
    LIMIT 10
) u
JOIN (
    SELECT id, type_name, ROW_NUMBER() OVER (ORDER BY id) AS rn
    FROM vehicle_type
) vt ON (u.rn - 1) % (SELECT COUNT(*) FROM vehicle_type) = vt.rn - 1;

-- Additional services for the 10 new driver vehicles
INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT v.id, s.id
FROM vehicle v
JOIN additional_service s ON s.name IN ('WiFi', 'Pet friendly', 'Baby seat')
WHERE v.license_plate LIKE 'NS-%-DR';

-- Give some variety: driver6 (Premium) gets all 5 services
INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT v.id, s.id
FROM vehicle v
JOIN app_user d ON d.id = v.driver_id
JOIN additional_service s ON s.name IN ('Smoking allowed', 'Wheelchair accessible')
WHERE d.email = 'driver6@test.com'
ON CONFLICT DO NOTHING;

-- driver1 (Premium) gets smoking allowed too
INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT v.id, s.id
FROM vehicle v
JOIN app_user d ON d.id = v.driver_id
JOIN additional_service s ON s.name = 'Smoking allowed'
WHERE d.email = 'driver1@test.com'
ON CONFLICT DO NOTHING;

-- =========================
-- LOCATIONS  (Novi Sad, Serbia — all real or plausible coordinates)
-- Geohash prefix u2n17 covers central Novi Sad (~45.24–45.27 N, 19.80–19.87 E)
-- =========================
INSERT INTO location (id, geo_hash, latitude, longitude, address) VALUES
-- ── Existing 8 ─────────────────────────────────────────────────
(nextval('location_seq'), 'u2n177jf9j1j', 45.236663, 19.838263, 'B dom'),
(nextval('location_seq'), 'u2n17szensm0', 45.246357, 19.851894, 'FTN'),
(nextval('location_seq'), 'u2n17w2ssdzj', 45.254847, 19.842198, 'Pozorište'),
(nextval('location_seq'), 'u2n17nfzd2vb', 45.258097, 19.823217, 'Sajam'),
(nextval('location_seq'), 'u2n1e0r3rvf6', 45.265291, 19.829628, 'Železnička'),
(nextval('location_seq'), 'u2n16kt0p5v2', 45.244466, 19.793551, 'JGSP'),
(nextval('location_seq'), 'u2n17jgr834m', 45.252606, 19.823813, 'Medicinski fakultet'),
(nextval('location_seq'), 'u2n17x4vb11m', 45.259172, 19.845087, 'Matica Srpska'),
-- ── New 12 ─────────────────────────────────────────────────────
(nextval('location_seq'), 'u2n17h4gt2k5', 45.251700, 19.836900, 'Trg slobode'),
(nextval('location_seq'), 'u2n174hr6k9z', 45.241200, 19.826300, 'Adamovićevo'),
(nextval('location_seq'), 'u2n17tbq5r4n', 45.259000, 19.835400, 'Bulevar Evrope'),
(nextval('location_seq'), 'u2n173hk8x5p', 45.235100, 19.845200, 'Liman 4'),
(nextval('location_seq'), 'u2n16rbt9v1s', 45.242100, 19.810200, 'Slana Bara'),
(nextval('location_seq'), 'u2n17q5c8d4k', 45.256300, 19.864100, 'Grbavica'),
(nextval('location_seq'), 'u2n1ekp3r9m2', 45.272400, 19.823100, 'Klisa'),
(nextval('location_seq'), 'u2n1e3sy8nk2', 45.278100, 19.868200, 'Novo Naselje'),
(nextval('location_seq'), 'u2n17g9m3p7e', 45.249100, 19.831100, 'Zmaj Jovina'),
(nextval('location_seq'), 'u2n16j5xr0p8', 45.243300, 19.797800, 'Futog put'),
(nextval('location_seq'), 'u2n1hb4kr7t3', 45.251700, 19.863200, 'Petrovaradin'),
(nextval('location_seq'), 'u2n17m6vp2s9', 45.248900, 19.856100, 'Dunavska');

-- =========================
-- ROUTES  (geohash = concatenation of waypoint geohashes)
-- =========================
INSERT INTO route (id, geo_hash) VALUES
-- ── Existing 4 ──────────────────────────────────────────────────
(nextval('route_seq'), 'u2n177jf9j1ju2n17szensm0'),
(nextval('route_seq'), 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb'),
(nextval('route_seq'), 'u2n17w2ssdzju2n16kt0p5v2u2n17x4vb11mu2n17nfzd2vbu2n17szensm0'),
(nextval('route_seq'), 'u2n16kt0p5v2u2n17nfzd2vbu2n177jf9j1ju2n17w2ssdzj'),
-- ── New 8 ───────────────────────────────────────────────────────
(nextval('route_seq'), 'u2n17h4gt2k5u2n17tbq5r4n'),
(nextval('route_seq'), 'u2n174hr6k9zu2n17jgr834mu2n17q5c8d4k'),
(nextval('route_seq'), 'u2n173hk8x5pu2n17nfzd2vbu2n17szensm0'),
(nextval('route_seq'), 'u2n16kt0p5v2u2n16rbt9v1su2n17jgr834mu2n17h4gt2k5'),
(nextval('route_seq'), 'u2n17g9m3p7eu2n17szensm0u2n17x4vb11m'),
(nextval('route_seq'), 'u2n1hb4kr7t3u2n17m6vp2s9u2n17szensm0'),
(nextval('route_seq'), 'u2n1ekp3r9m2u2n1e0r3rvf6u2n17jgr834m'),
(nextval('route_seq'), 'u2n16j5xr0p8u2n16kt0p5v2u2n174hr6k9zu2n17g9m3p7e');

-- ===========================================================================
-- RIDES  (2026-01-01 → 2026-02-18)
--
-- Driver → Vehicle Type → Price per km:
--   driver@test.com  → Standard  → 30.00
--   driver1@test.com → Premium   → 60.00
--   driver2@test.com → Van       → 50.00
--   driver3@test.com → Electric  → 45.00
--   driver4@test.com → Comfort   → 40.00
--   driver5@test.com → Standard  → 30.00
--   driver6@test.com → Premium   → 60.00
--   driver7@test.com → Van       → 50.00
--   driver8@test.com → Electric  → 45.00
--   driver9@test.com → Comfort   → 40.00
--   driver10@test.com→ Standard  → 30.00
--
-- Path geohash = route geohash with minor character changes (driver's actual GPS trace)
-- total_cost = price_per_km * distance_km
-- CANCELLED rides: no start_time / end_time / distance / total_cost
-- ===========================================================================

-- ─── Ride 1 – 2026-01-01 – FINISHED ───────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-01 09:52:00', NULL,
    '2026-01-01 10:00:00', '2026-01-01 10:14:00',
    'FINISHED',
    'u2n177jf9h8fu2n17szens3m1',   -- slight deviation from route 1
    4.2, 30.00, 126.00,
    (SELECT id FROM app_user WHERE email = 'driver@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer1@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n177jf9j1ju2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer1@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Odličan vozač, stigli smo brzo!');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name = 'WiFi';

-- ─── Ride 2 – 2026-01-02 – FINISHED ───────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-02 14:25:00', NULL,
    '2026-01-02 14:30:00', '2026-01-02 14:51:00',
    'FINISHED',
    'u2n1e0r3rwf6u2n17jgr835mu2n17nfzd3vc',  -- slight deviation from route 2
    6.5, 60.00, 390.00,
    (SELECT id FROM app_user WHERE email = 'driver1@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer2@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM route WHERE geo_hash = 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer2@test.com'),
    currval('ride_seq'), NULL, 4, 5, 'Prelepo vozilo, udobno putovanje.');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Pet friendly');

-- ─── Ride 3 – 2026-01-03 – FINISHED ───────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-03 08:10:00', '2026-01-03 08:30:00',
    '2026-01-03 08:30:00', '2026-01-03 09:00:00',
    'FINISHED',
    'u2n17w2ssezku2n16kt0p6v2u2n17x4vb12mu2n17nfzd3vbu2n17szens1m0',
    9.1, 50.00, 455.00,
    (SELECT id FROM app_user WHERE email = 'driver2@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer3@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Van'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17w2ssdzju2n16kt0p5v2u2n17x4vb11mu2n17nfzd2vbu2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer3@test.com'),
    currval('ride_seq'), NULL, 5, 4, 'Vozač je bio veoma ljubazan, preporučujem.');

-- ─── Ride 4 – 2026-01-04 – FINISHED ───────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-04 11:55:00', NULL,
    '2026-01-04 12:00:00', '2026-01-04 12:16:00',
    'FINISHED',
    'u2n16kt0p6v3u2n17nfzd3vcu2n177jf9h8fu2n17w2ssezj',
    5.3, 45.00, 238.50,
    (SELECT id FROM app_user WHERE email = 'driver3@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Electric'),
    (SELECT id FROM route WHERE geo_hash = 'u2n16kt0p5v2u2n17nfzd2vbu2n177jf9j1ju2n17w2ssdzj')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Tih elektro-automobil, impresivno!');

-- ─── Ride 5 – 2026-01-05 – FINISHED ───────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-05 17:48:00', NULL,
    '2026-01-05 18:00:00', '2026-01-05 18:24:00',
    'FINISHED',
    'u2n17h4gw2k6u2n17tbqs5n',
    7.8, 40.00, 312.00,
    (SELECT id FROM app_user WHERE email = 'driver4@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer4@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17h4gt2k5u2n17tbq5r4n')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer4@test.com'),
    currval('ride_seq'), NULL, 4, 4, 'Solidna vožnja, malo smo kasnili.');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name = 'Baby seat';

-- ─── Ride 6 – 2026-01-07 – INTERRUPTED ────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-07 09:00:00', NULL,
    '2026-01-07 09:05:00', '2026-01-07 09:12:00',
    'INTERRUPTED',
    'u2n177jf9h8f',   -- driver only covered part of the route
    2.1, 30.00, 63.00,
    'Vozač je morao da zaustavi vožnju zbog kvara na vozilu.',
    (SELECT id FROM app_user WHERE email = 'driver5@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer5@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n177jf9j1ju2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer5@test.com'),
    currval('ride_seq'),
    'Vozilo se pokvarilo na pola puta, morao sam da tražim drugi prevoz.',
    2, 1, 'Razočaranje, auto se pokvario.');

-- ─── Ride 7 – 2026-01-08 – CANCELLED (by user) ────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-08 15:00:00', '2026-01-08 15:30:00', NULL, NULL,
    'CANCELLED',
    'Korisnik otkazao - promenio planove.',
    (SELECT id FROM app_user WHERE email = 'driver6@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer6@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM route WHERE geo_hash = 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer6@test.com'),
    currval('ride_seq'), NULL, NULL, NULL, NULL);

-- ─── Ride 8 – 2026-01-09 – FINISHED ───────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-09 19:50:00', NULL,
    '2026-01-09 20:00:00', '2026-01-09 20:26:00',
    'FINISHED',
    'u2n174hr7k9zu2n17jgr836mu2n17q5c9d4k',
    8.4, 50.00, 420.00,
    (SELECT id FROM app_user WHERE email = 'driver7@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer7@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Van'),
    (SELECT id FROM route WHERE geo_hash = 'u2n174hr6k9zu2n17jgr834mu2n17q5c8d4k')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer7@test.com'),
    currval('ride_seq'), NULL, 5, 4, 'Kombi je bio čist i prostran. Vozač strpljiv.');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Baby seat');

-- ─── Ride 9 – 2026-01-10 – FINISHED ───────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-10 07:55:00', '2026-01-10 08:00:00',
    '2026-01-10 08:00:00', '2026-01-10 08:18:00',
    'FINISHED',
    'u2n173hk9x5qu2n17nfzd3vcu2n17szens1m1',
    6.0, 45.00, 270.00,
    (SELECT id FROM app_user WHERE email = 'driver8@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer8@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Electric'),
    (SELECT id FROM route WHERE geo_hash = 'u2n173hk8x5pu2n17nfzd2vbu2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer8@test.com'),
    currval('ride_seq'), NULL, 4, 5, 'Tačno na vreme. Elektro vozilo je fenomenalno.');

-- ─── Ride 10 – 2026-01-12 – CANCELLED (by driver) ─────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-12 10:20:00', NULL, NULL, NULL,
    'CANCELLED',
    'Vozač otkazao - hitna situacija u porodici.',
    (SELECT id FROM app_user WHERE email = 'driver9@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17h4gt2k5u2n17tbq5r4n')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    currval('ride_seq'), NULL, NULL, NULL, NULL);

-- ─── Ride 11 – 2026-01-14 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-14 13:42:00', NULL,
    '2026-01-14 14:00:00', '2026-01-14 14:17:00',
    'FINISHED',
    'u2n16kt0p6v2u2n16rbt0v2su2n17jgr835mu2n17h4gw2k6',
    5.5, 30.00, 165.00,
    (SELECT id FROM app_user WHERE email = 'driver10@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer9@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n16kt0p5v2u2n16rbt9v1su2n17jgr834mu2n17h4gt2k5')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer9@test.com'),
    currval('ride_seq'), NULL, 3, 3, 'Prosečna vožnja, ništa posebno.');

-- ─── Ride 12 – 2026-01-15 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-15 11:00:00', NULL,
    '2026-01-15 11:05:00', '2026-01-15 11:17:00',
    'FINISHED',
    'u2n17g9m4p7eu2n17szens2m0u2n17x4vb12m',
    3.9, 30.00, 117.00,
    (SELECT id FROM app_user WHERE email = 'driver@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer10@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17g9m3p7eu2n17szensm0u2n17x4vb11m')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer10@test.com'),
    currval('ride_seq'), NULL, 5, 4, 'Kratka ali prijatna vožnja.');

-- ─── Ride 13 – 2026-01-16 – INTERRUPTED ───────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-16 21:00:00', NULL,
    '2026-01-16 21:10:00', '2026-01-16 21:16:00',
    'INTERRUPTED',
    'u2n1e0r3rwf7',
    1.8, 60.00, 108.00,
    'Putnik zatražio da se zaustavi pre destinacije.',
    (SELECT id FROM app_user WHERE email = 'driver1@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer1@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM route WHERE geo_hash = 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer1@test.com'),
    currval('ride_seq'),
    'Zaustavio sam vožnju pre odredišta jer mi se hitno javila porodica.',
    4, 5, 'Vozač razumeo situaciju, bez problema.');

-- ─── Ride 14 – 2026-01-18 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-18 09:50:00', '2026-01-18 10:00:00',
    '2026-01-18 10:00:00', '2026-01-18 10:13:00',
    'FINISHED',
    'u2n177jf9h9fu2n17szens4m0',
    4.2, 50.00, 210.00,
    (SELECT id FROM app_user WHERE email = 'driver2@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer2@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Van'),
    (SELECT id FROM route WHERE geo_hash = 'u2n177jf9j1ju2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer2@test.com'),
    currval('ride_seq'), NULL, 4, 3, 'Vozač u redu, ali kombi pomalo zastario.');

-- ─── Ride 15 – 2026-01-19 – PANICKED ─────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-19 22:00:00', NULL,
    '2026-01-19 22:10:00', '2026-01-19 22:22:00',
    'PANICKED',
    'u2n1e0r3rwf6u2n17jgr835m',
    3.5, 45.00, 157.50,
    'Vozač je pritisnuo dugme za paniku zbog agresivnog ponašanja putnika.',
    (SELECT id FROM app_user WHERE email = 'driver3@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer3@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Electric'),
    (SELECT id FROM route WHERE geo_hash = 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer3@test.com'),
    currval('ride_seq'),
    'Sporni incident tokom vožnje, istraga u toku.',
    NULL, NULL, NULL);

-- ─── Ride 16 – 2026-01-21 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-21 07:30:00', NULL,
    '2026-01-21 07:35:00', '2026-01-21 08:03:00',
    'FINISHED',
    'u2n17w2ssfzju2n16kt0p6v2u2n17x4vb12mu2n17nfzd3vbu2n17szens1m1',
    9.1, 40.00, 364.00,
    (SELECT id FROM app_user WHERE email = 'driver4@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer4@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17w2ssdzju2n16kt0p5v2u2n17x4vb11mu2n17nfzd2vbu2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer4@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Savršena jutarnja vožnja, tačno na posao!');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name = 'WiFi';

-- ─── Ride 17 – 2026-01-22 – CANCELLED (by user) ───────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-22 16:00:00', '2026-01-22 17:00:00', NULL, NULL,
    'CANCELLED',
    'Korisnik otkazao - našao alternativni prevoz.',
    (SELECT id FROM app_user WHERE email = 'driver5@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer5@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n16kt0p5v2u2n17nfzd2vbu2n177jf9j1ju2n17w2ssdzj')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer5@test.com'),
    currval('ride_seq'), NULL, NULL, NULL, NULL);

-- ─── Ride 18 – 2026-01-23 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-23 18:45:00', NULL,
    '2026-01-23 19:00:00', '2026-01-23 19:24:00',
    'FINISHED',
    'u2n17h4gw3k5u2n17tbqs6n',
    7.8, 60.00, 468.00,
    (SELECT id FROM app_user WHERE email = 'driver6@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer6@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17h4gt2k5u2n17tbq5r4n')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer6@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Luksuzno iskustvo, preporučujem svima!');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Smoking allowed');

-- ─── Ride 19 – 2026-01-25 – INTERRUPTED ───────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-25 10:30:00', NULL,
    '2026-01-25 10:35:00', '2026-01-25 10:45:00',
    'INTERRUPTED',
    'u2n174hr7k9z',
    3.2, 50.00, 160.00,
    'Saobraćajna nezgoda blokirala pravac, vožnja prekinuta.',
    (SELECT id FROM app_user WHERE email = 'driver7@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Van'),
    (SELECT id FROM route WHERE geo_hash = 'u2n174hr6k9zu2n17jgr834mu2n17q5c8d4k')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    currval('ride_seq'),
    'Vožnja prekinuta zbog saobraćajne nezgode na ruti.',
    3, 4, 'Nije krivica vozača, situacija na putu.');

-- ─── Ride 20 – 2026-01-26 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-26 13:00:00', NULL,
    '2026-01-26 13:05:00', '2026-01-26 13:23:00',
    'FINISHED',
    'u2n173hk9x6pu2n17nfzd3vcu2n17szens1m2',
    5.7, 45.00, 256.50,
    (SELECT id FROM app_user WHERE email = 'driver8@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer7@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Electric'),
    (SELECT id FROM route WHERE geo_hash = 'u2n173hk8x5pu2n17nfzd2vbu2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer7@test.com'),
    currval('ride_seq'), NULL, 4, 5, 'Tiho i brzo, elektro je budućnost.');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name = 'Pet friendly';

-- ─── Ride 21 – 2026-01-28 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-28 08:55:00', '2026-01-28 09:00:00',
    '2026-01-28 09:00:00', '2026-01-28 09:19:00',
    'FINISHED',
    'u2n16kt0p6v3u2n16rbt0v2su2n17jgr836mu2n17h4gw3k5',
    6.3, 40.00, 252.00,
    (SELECT id FROM app_user WHERE email = 'driver9@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer8@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'),
    (SELECT id FROM route WHERE geo_hash = 'u2n16kt0p5v2u2n16rbt9v1su2n17jgr834mu2n17h4gt2k5')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer8@test.com'),
    currval('ride_seq'), NULL, 5, 4, 'Vozač ljubazan i profesionalan.');

-- ─── Ride 22 – 2026-01-30 – CANCELLED (by driver) ─────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-30 11:00:00', NULL, NULL, NULL,
    'CANCELLED',
    'Vozač otkazao - vozilo na tehničkom pregledu.',
    (SELECT id FROM app_user WHERE email = 'driver10@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer9@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n177jf9j1ju2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer9@test.com'),
    currval('ride_seq'), NULL, NULL, NULL, NULL);

-- ─── Ride 23 – 2026-01-31 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-01-31 16:50:00', NULL,
    '2026-01-31 17:00:00', '2026-01-31 17:15:00',
    'FINISHED',
    'u2n1e0r3rwg6u2n17jgr836mu2n17nfzd3vc',
    4.8, 30.00, 144.00,
    (SELECT id FROM app_user WHERE email = 'driver@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer10@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer10@test.com'),
    currval('ride_seq'), NULL, 4, 4, 'Dobra vožnja za kraj januara.');

-- ─── Ride 24 – 2026-02-01 – PANICKED ─────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-01 23:00:00', NULL,
    '2026-02-01 23:10:00', '2026-02-01 23:22:00',
    'PANICKED',
    'u2n17w2ssfzj',
    2.9, 60.00, 174.00,
    'Vozač pritisnuo dugme za paniku - putnik odbijao da napusti vozilo na odredištu.',
    (SELECT id FROM app_user WHERE email = 'driver1@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer1@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17w2ssdzju2n16kt0p5v2u2n17x4vb11mu2n17nfzd2vbu2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer1@test.com'),
    currval('ride_seq'),
    'Incident prijavljen. Nalog korisnika suspendovan na istrazi.',
    NULL, NULL, NULL);

-- ─── Ride 25 – 2026-02-03 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-03 12:00:00', NULL,
    '2026-02-03 12:05:00', '2026-02-03 12:27:00',
    'FINISHED',
    'u2n177jf9h8fu2n17szens3m2',
    7.1, 50.00, 355.00,
    (SELECT id FROM app_user WHERE email = 'driver2@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer2@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Van'),
    (SELECT id FROM route WHERE geo_hash = 'u2n177jf9j1ju2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer2@test.com'),
    currval('ride_seq'), NULL, 5, 4, 'Odlično, kao uvek sa ovim vozačem.');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name = 'WiFi';

-- ─── Ride 26 – 2026-02-04 – INTERRUPTED ───────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-04 08:00:00', NULL,
    '2026-02-04 08:05:00', '2026-02-04 08:13:00',
    'INTERRUPTED',
    'u2n1e0r3rwf6',
    2.5, 45.00, 112.50,
    'Pucanje gume na putu, vožnja prekinuta hitno.',
    (SELECT id FROM app_user WHERE email = 'driver3@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer3@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Electric'),
    (SELECT id FROM route WHERE geo_hash = 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer3@test.com'),
    currval('ride_seq'),
    'Guma pukla, vozač odmah zaustavio i pozvao pomoć.',
    4, 3, 'Vozač reagovao profesionalno u neprijatnoj situaciji.');

-- ─── Ride 27 – 2026-02-05 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-05 09:55:00', '2026-02-05 10:00:00',
    '2026-02-05 10:00:00', '2026-02-05 10:18:00',
    'FINISHED',
    'u2n17h4gw2k6u2n17tbqs5m',
    6.0, 40.00, 240.00,
    (SELECT id FROM app_user WHERE email = 'driver4@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17h4gt2k5u2n17tbq5r4n')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Uvek tačan, uvek ljubazan. Preporučujem!');

-- ─── Ride 28 – 2026-02-07 – CANCELLED (by user) ───────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-07 14:30:00', NULL, NULL, NULL,
    'CANCELLED',
    'Korisnik otkazao - zaboravio na zakazanu vožnju.',
    (SELECT id FROM app_user WHERE email = 'driver5@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer4@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n173hk8x5pu2n17nfzd2vbu2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer4@test.com'),
    currval('ride_seq'), NULL, NULL, NULL, NULL);

-- ─── Ride 29 – 2026-02-08 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-08 20:00:00', NULL,
    '2026-02-08 20:05:00', '2026-02-08 20:32:00',
    'FINISHED',
    'u2n174hr7k9zu2n17jgr836mu2n17q5c9d4k',
    8.9, 60.00, 534.00,
    (SELECT id FROM app_user WHERE email = 'driver6@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer5@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM route WHERE geo_hash = 'u2n174hr6k9zu2n17jgr834mu2n17q5c8d4k')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer5@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Premium iskustvo od A do Ž. Hvala!');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Wheelchair accessible');

-- ─── Ride 30 – 2026-02-10 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-10 10:45:00', NULL,
    '2026-02-10 11:00:00', '2026-02-10 11:13:00',
    'FINISHED',
    'u2n177jf9h8eu2n17szens3m0',
    4.2, 50.00, 210.00,
    (SELECT id FROM app_user WHERE email = 'driver7@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer6@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Van'),
    (SELECT id FROM route WHERE geo_hash = 'u2n177jf9j1ju2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer6@test.com'),
    currval('ride_seq'), NULL, 4, 4, 'Pouzdano i tačno, zadovoljan sam.');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name = 'Pet friendly';

-- ─── Ride 31 – 2026-02-12 – CANCELLED (by driver) ─────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-12 09:00:00', NULL, NULL, NULL,
    'CANCELLED',
    'Vozač otkazao - iznenadna bolest.',
    (SELECT id FROM app_user WHERE email = 'driver8@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer7@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Electric'),
    (SELECT id FROM route WHERE geo_hash = 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer7@test.com'),
    currval('ride_seq'), NULL, NULL, NULL, NULL);

-- ─── Ride 32 – 2026-02-14 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-14 17:00:00', NULL,
    '2026-02-14 17:05:00', '2026-02-14 17:29:00',
    'FINISHED',
    'u2n17w2ssfzku2n16kt0p7v2u2n17x4vb13mu2n17nfzd4vbu2n17szens1m1',
    7.5, 40.00, 300.00,
    (SELECT id FROM app_user WHERE email = 'driver9@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer8@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17w2ssdzju2n16kt0p5v2u2n17x4vb11mu2n17nfzd2vbu2n17szensm0')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer8@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Valentinovo + savršena vožnja = savršen dan!');

-- ─── Ride 33 – 2026-02-15 – PANICKED ─────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  cancellation_reason,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-15 01:00:00', NULL,
    '2026-02-15 01:10:00', '2026-02-15 01:20:00',
    'PANICKED',
    'u2n16kt0p6v2',
    3.1, 30.00, 93.00,
    'Noćna vožnja - vozač pritisnuo dugme za paniku, putnik pod uticajem alkohola.',
    (SELECT id FROM app_user WHERE email = 'driver10@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer9@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n16kt0p5v2u2n16rbt9v1su2n17jgr834mu2n17h4gt2k5')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer9@test.com'),
    currval('ride_seq'),
    'Prijava podneta, korisnik opomenut zbog ponašanja.',
    NULL, NULL, NULL);

-- ─── Ride 34 – 2026-02-16 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-16 11:00:00', '2026-02-16 11:30:00',
    '2026-02-16 11:30:00', '2026-02-16 11:47:00',
    'FINISHED',
    'u2n17g9m4p7fu2n17szens2m1u2n17x4vb12m',
    5.2, 30.00, 156.00,
    (SELECT id FROM app_user WHERE email = 'driver@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer10@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Standard'),
    (SELECT id FROM route WHERE geo_hash = 'u2n17g9m3p7eu2n17szensm0u2n17x4vb11m')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer10@test.com'),
    currval('ride_seq'), NULL, 5, 4, 'Odlično jutro, hvala vozaču!');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name = 'WiFi';

-- ─── Ride 35 – 2026-02-18 – FINISHED ──────────────────────────────────────
INSERT INTO ride (id, created_at, scheduled_time, start_time, end_time,
                  status, path, distance_km, price, total_cost,
                  driver_id, ride_owner_id, vehicle_type_id, route_id)
VALUES (
    nextval('ride_seq'),
    '2026-02-18 14:40:00', NULL,
    '2026-02-18 15:00:00', '2026-02-18 15:15:00',
    'FINISHED',
    'u2n16kt0p6v2u2n16rbt0v2su2n17jgr836mu2n17h4gw3k5',
    4.7, 60.00, 282.00,
    (SELECT id FROM app_user WHERE email = 'driver1@test.com'),
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM route WHERE geo_hash = 'u2n16kt0p5v2u2n16rbt9v1su2n17jgr834mu2n17h4gt2k5')
);
INSERT INTO passenger (id, email, access_token, user_id, ride_id, inconsistency_note, driver_rating, vehicle_rating, comment)
VALUES (nextval('passenger_seq'), NULL, NULL,
    (SELECT id FROM app_user WHERE email = 'customer@test.com'),
    currval('ride_seq'), NULL, 5, 5, 'Opet fantastičan Premium. Najdrži vozač!');
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT currval('ride_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Smoking allowed', 'Pet friendly');

-- ===========================================================================
-- UPDATE REQUESTS  (some drivers requesting profile/vehicle changes, no img)
-- ===========================================================================
INSERT INTO update_request (id, first_name, last_name, email, address, phone_number, img_src,
                             model, license_plate, number_of_seats, vehicle_type_id, driver_id)
VALUES (
    nextval('update_request_seq'),
    'Nikola', 'Jovanović', 'driver1@test.com', 'Jevrejska 12, Novi Sad', '+38163100001', NULL,
    'BMW 5 Series', 'NS-BMW-01', 4,
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM app_user WHERE email = 'driver1@test.com')
);
INSERT INTO update_request_additional_services (update_request_id, additional_services_id)
SELECT currval('update_request_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Smoking allowed', 'Pet friendly');

INSERT INTO update_request (id, first_name, last_name, email, address, phone_number, img_src,
                             model, license_plate, number_of_seats, vehicle_type_id, driver_id)
VALUES (
    nextval('update_request_seq'),
    'Stefan', 'Marković', 'driver4@test.com', 'Futog 45, Novi Sad', '+38163200002', NULL,
    'Mercedes-Benz C-Class', 'NS-MRC-04', 4,
    (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'),
    (SELECT id FROM app_user WHERE email = 'driver4@test.com')
);
INSERT INTO update_request_additional_services (update_request_id, additional_services_id)
SELECT currval('update_request_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Baby seat');

INSERT INTO update_request (id, first_name, last_name, email, address, phone_number, img_src,
                             model, license_plate, number_of_seats, vehicle_type_id, driver_id)
VALUES (
    nextval('update_request_seq'),
    'Ana', 'Petrović', 'driver6@test.com', 'Bulevar Oslobođenja 31, Novi Sad', '+38163300003', NULL,
    'Audi A8', 'NS-AUD-06', 4,
    (SELECT id FROM vehicle_type WHERE type_name = 'Premium'),
    (SELECT id FROM app_user WHERE email = 'driver6@test.com')
);
INSERT INTO update_request_additional_services (update_request_id, additional_services_id)
SELECT currval('update_request_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Smoking allowed', 'Wheelchair accessible');

INSERT INTO update_request (id, first_name, last_name, email, address, phone_number, img_src,
                             model, license_plate, number_of_seats, vehicle_type_id, driver_id)
VALUES (
    nextval('update_request_seq'),
    'Milan', 'Nikolić', 'driver8@test.com', 'Zmaj Jovina 7, Novi Sad', '+38163400004', NULL,
    'Tesla Model 3', 'NS-TES-08', 4,
    (SELECT id FROM vehicle_type WHERE type_name = 'Electric'),
    (SELECT id FROM app_user WHERE email = 'driver8@test.com')
);
INSERT INTO update_request_additional_services (update_request_id, additional_services_id)
SELECT currval('update_request_seq'), id FROM additional_service WHERE name IN ('WiFi', 'Pet friendly');

-- ===========================================================================
-- BLOCK SOME USERS  (for visual variety in admin user table)
-- ===========================================================================

-- Block customer3 (involved in PANICKED ride 15, account under review)
UPDATE app_user
SET is_blocked = true,
    block_reason = 'Nalog blokiran na osnovu prijave vozača (incident 2026-01-19). Istraga u toku.'
WHERE email = 'customer3@test.com';

-- Block customer1 (involved in PANICKED ride 24, refused to leave vehicle)
UPDATE app_user
SET is_blocked = true,
    block_reason = 'Korisnik odbio da napusti vozilo na odredištu. Blokiran do završetka istrage.'
WHERE email = 'customer1@test.com';

-- Block customer9 (involved in PANICKED ride 33, intoxicated behaviour)
UPDATE app_user
SET is_blocked = true,
    block_reason = 'Agresivno ponašanje tokom noćne vožnje (2026-02-15). Privremena blokada.'
WHERE email = 'customer9@test.com';

-- Block driver3 (involved in PANICKED ride 15 — may have provoked passenger)
UPDATE app_user
SET is_blocked = true,
    block_reason = 'Prijavljen incident tokom vožnje. Nalog suspendovan na vreme trajanja istrage.'
WHERE email = 'driver3@test.com';

-- ===========================================================================
-- DRIVER STATUS ADJUSTMENTS
-- (Most drivers already set to ACTIVE in the generate_series block;
--  driver2 and driver9 → INACTIVE; driver10 → WAITING_ACTIVATION)
-- ===========================================================================
UPDATE app_user SET driver_status = 'INACTIVE'           WHERE email = 'driver2@test.com';
UPDATE app_user SET driver_status = 'INACTIVE'           WHERE email = 'driver9@test.com';
UPDATE app_user SET driver_status = 'WAITING_ACTIVATION' WHERE email = 'driver10@test.com';
UPDATE app_user SET driver_status = 'BUSY'               WHERE email = 'driver6@test.com';
