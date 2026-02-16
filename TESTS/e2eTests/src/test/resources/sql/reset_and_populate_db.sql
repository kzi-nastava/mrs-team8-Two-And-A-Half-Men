-- =========================
-- DROP ALL DATA (KEEP TABLES)
-- =========================

TRUNCATE TABLE
    app_user,
    vehicle,
    ride,
    location,
    route,
    additional_service,
    ride_additional_services,
    vehicle_additional_services,
    message,
    support_chat,
    notification,
    passenger,
    driver_activity,
    update_request,
    vehicle_type
CASCADE;

-- Reset sequences
ALTER SEQUENCE additional_service_seq RESTART WITH 1;
ALTER SEQUENCE vehicle_type_seq RESTART WITH 1;
ALTER SEQUENCE app_user_seq RESTART WITH 1;
ALTER SEQUENCE vehicle_seq RESTART WITH 1;
ALTER SEQUENCE location_seq RESTART WITH 1;
ALTER SEQUENCE route_seq RESTART WITH 1;
ALTER SEQUENCE ride_seq RESTART WITH 1;
ALTER SEQUENCE passenger_seq RESTART WITH 1;
ALTER SEQUENCE driver_activity_seq RESTART WITH 1;
ALTER SEQUENCE notification_seq RESTART WITH 1;
ALTER SEQUENCE support_chat_seq RESTART WITH 1;
ALTER SEQUENCE message_seq RESTART WITH 1;
ALTER SEQUENCE update_request_seq RESTART WITH 1;

-- =========================
-- POPULATE REFERENCE DATA
-- =========================

INSERT INTO additional_service (id, name, description) VALUES
                                                           (nextval('additional_service_seq'), 'Pet friendly', 'Pets are allowed during the ride as long as they are properly secured.'),
                                                           (nextval('additional_service_seq'), 'Baby seat', 'Vehicle is equipped with a certified child safety seat.'),
                                                           (nextval('additional_service_seq'), 'Smoking allowed', 'Smoking is permitted inside the vehicle during the ride.'),
                                                           (nextval('additional_service_seq'), 'Wheelchair accessible', 'Vehicle supports wheelchair access with ramp or lift.'),
                                                           (nextval('additional_service_seq'), 'WiFi', 'Free onboard WiFi available for passengers.');

INSERT INTO vehicle_type (id, type_name, description, price) VALUES
                                                                 (nextval('vehicle_type_seq'), 'Standard', 'Standard passenger vehicle for everyday rides.', 1.00),
                                                                 (nextval('vehicle_type_seq'), 'Comfort', 'More spacious and comfortable vehicle with extra legroom.', 1.30),
                                                                 (nextval('vehicle_type_seq'), 'Premium', 'High-end vehicle with luxury interior and superior comfort.', 1.70),
                                                                 (nextval('vehicle_type_seq'), 'Van', 'Larger vehicle suitable for groups or extra luggage.', 1.50),
                                                                 (nextval('vehicle_type_seq'), 'Electric', 'Eco-friendly electric vehicle with zero emissions.', 1.40);

-- =========================
-- LOCATIONS (Real Novi Sad locations with proper geohash)
-- =========================

INSERT INTO location (id, geo_hash, latitude, longitude, address) VALUES
-- City center and main landmarks
(nextval('location_seq'), 'u2n177jf9j1j', 45.236663, 19.838263, 'Bulevar Oslobođenja (B Dom)'),
(nextval('location_seq'), 'u2n17szensm0', 45.246357, 19.851894, 'Fakultet Tehničkih Nauka (FTN)'),
(nextval('location_seq'), 'u2n17w2ssdzj', 45.254847, 19.842198, 'Srpsko Narodno Pozorište'),
(nextval('location_seq'), 'u2n17nfzd2vb', 45.258097, 19.823217, 'Novosadski Sajam'),
(nextval('location_seq'), 'u2n1e0r3rvf6', 45.265291, 19.829628, 'Železnička Stanica'),
(nextval('location_seq'), 'u2n16kt0p5v2', 45.244466, 19.793551, 'JGSP Garage'),
(nextval('location_seq'), 'u2n17jgr834m', 45.252606, 19.823813, 'Medicinski Fakultet'),
(nextval('location_seq'), 'u2n17x4vb11m', 45.259172, 19.845087, 'Matica Srpska'),
-- Additional locations for more route variety
(nextval('location_seq'), 'u2n17tqf5km3', 45.251667, 19.836944, 'Trg Slobode'),
(nextval('location_seq'), 'u2n17w9h2k5p', 45.255833, 19.845556, 'Petrovaradinska Tvrđava'),
(nextval('location_seq'), 'u2n177k8nh67', 45.238889, 19.841111, 'Štrand Beach'),
(nextval('location_seq'), 'u2n17m3k9sd2', 45.253333, 19.827778, 'BIG Shopping Center'),
(nextval('location_seq'), 'u2n17u5k2p8m', 45.248611, 19.847222, 'Dunavski Park'),
(nextval('location_seq'), 'u2n1e1k7mp4n', 45.267222, 19.833333, 'Liman Neighborhood'),
(nextval('location_seq'), 'u2n16h8k3pm9', 45.241667, 19.791667, 'Veternik'),
(nextval('location_seq'), 'u2n17r9k5nm2', 45.250556, 19.835000, 'Zmaj Jovina Street'),
(nextval('location_seq'), 'u2n17k4n8pm6', 45.247778, 19.825556, 'Futog Road'),
(nextval('location_seq'), 'u2n1e3k9pm4s', 45.270000, 19.835000, 'Novo Naselje'),
(nextval('location_seq'), 'u2n17p8k4nm7', 45.249444, 19.832222, 'Bulevar Cara Lazara'),
(nextval('location_seq'), 'u2n18k5pm3n8', 45.262778, 19.851111, 'Klisa');

-- =========================
-- ROUTES (Common routes in Novi Sad)
-- =========================

INSERT INTO route (id, geo_hash) VALUES
-- Short routes
(nextval('route_seq'), 'u2n177jf9j1ju2n17szensm0'), -- B Dom to FTN
(nextval('route_seq'), 'u2n17w2ssdzju2n17tqf5km3'), -- Theatre to Trg Slobode
(nextval('route_seq'), 'u2n17nfzd2vbu2n17jgr834m'), -- Sajam to Medical Faculty
(nextval('route_seq'), 'u2n1e0r3rvf6u2n17x4vb11m'), -- Train Station to Matica Srpska
-- Medium routes
(nextval('route_seq'), 'u2n177k8nh67u2n17m3k9sd2u2n17nfzd2vb'), -- Strand to BIG to Sajam
(nextval('route_seq'), 'u2n16kt0p5v2u2n17jgr834mu2n17w2ssdzj'), -- JGSP to Medical to Theatre
(nextval('route_seq'), 'u2n17szensm0u2n17u5k2p8mu2n17w9h2k5p'), -- FTN to Dunavski Park to Fortress
(nextval('route_seq'), 'u2n17tqf5km3u2n17r9k5nm2u2n177jf9j1j'), -- Trg Slobode to Zmaj Jovina to B Dom
-- Long routes
(nextval('route_seq'), 'u2n16h8k3pm9u2n16kt0p5v2u2n17m3k9sd2u2n17tqf5km3u2n17w9h2k5p'), -- Veternik to JGSP to BIG to Trg to Fortress
(nextval('route_seq'), 'u2n1e1k7mp4nu2n1e0r3rvf6u2n17jgr834mu2n17w2ssdzju2n17szensm0'), -- Liman to Station to Medical to Theatre to FTN
(nextval('route_seq'), 'u2n177k8nh67u2n177jf9j1ju2n17tqf5km3u2n17x4vb11mu2n1e0r3rvf6'), -- Strand to B Dom to Trg to Matica to Station
(nextval('route_seq'), 'u2n18k5pm3n8u2n1e3k9pm4su2n1e1k7mp4nu2n17u5k2p8m'), -- Klisa to Novo Naselje to Liman to Dunavski Park
-- Cross-city routes
(nextval('route_seq'), 'u2n16h8k3pm9u2n17k4n8pm6u2n17p8k4nm7u2n17tqf5km3'), -- Veternik to Futog Road to Cara Lazara to Trg
(nextval('route_seq'), 'u2n17szensm0u2n17m3k9sd2u2n16kt0p5v2u2n16h8k3pm9'), -- FTN to BIG to JGSP to Veternik
(nextval('route_seq'), 'u2n18k5pm3n8u2n17w9h2k5pu2n17u5k2p8mu2n17r9k5nm2'); -- Klisa to Fortress to Dunavski Park to Zmaj Jovina

-- =========================
-- USERS
-- =========================

-- Initial test users
INSERT INTO app_user (
    id, role, first_name, last_name, email, password,
    address, phone_number, img_src,
    token, token_expiration,
    is_active, is_blocked
) VALUES
-- ADMIN
(
    nextval('app_user_seq'),
    'ADMIN',
    'Admin',
    'User',
    'admin@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Admin Street 1',
    '+38160000001',
    NULL, NULL, NULL, true, false
),
-- CUSTOMER
(
    nextval('app_user_seq'),
    'CUSTOMER',
    'John',
    'Doe',
    'customer@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Customer Street 5',
    '+38160000002',
    NULL, NULL, NULL, true, false
),
-- DRIVER
(
    nextval('app_user_seq'),
    'DRIVER',
    'Mike',
    'Driver',
    'driver@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Driver Street 10',
    '+38160000003',
    NULL, NULL, NULL, true, false
);

-- Update driver status for the initial driver
UPDATE app_user SET driver_status = 'ACTIVE' WHERE email = 'driver@test.com';

-- Generate 10 more admins
INSERT INTO app_user (
    id, role, first_name, last_name, email, password,
    address, phone_number, is_active, is_blocked
)
SELECT
    nextval('app_user_seq'),
    'ADMIN',
    'Admin' || i,
    'User',
    'admin' || i || '@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Admin Street ' || i,
    '+38160001' || LPAD(i::text, 2, '0'),
    true, false
FROM generate_series(1, 10) i;

-- Generate 15 customers
INSERT INTO app_user (
    id, role, first_name, last_name, email, password,
    address, phone_number, is_active, is_blocked
)
SELECT
    nextval('app_user_seq'),
    'CUSTOMER',
    'Customer' || i,
    'User',
    'customer' || i || '@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Customer Street ' || i,
    '+38160002' || LPAD(i::text, 2, '0'),
    true, false
FROM generate_series(1, 15) i;

-- Generate 12 drivers (including the initial one, we'll have 13 total)
INSERT INTO app_user (
    id, role, first_name, last_name, email, password,
    address, phone_number, is_active, is_blocked, driver_status
)
SELECT
    nextval('app_user_seq'),
    'DRIVER',
    'Driver' || i,
    'User',
    'driver' || i || '@test.com',
    '$2a$10$Yi2yXXIuKXOn9QlRMGdeHOEBymBsRnYBpJ/QoQvJoYfg5LpHCwOte',
    'Driver Street ' || i,
    '+38160003' || LPAD(i::text, 2, '0'),
    true,
    false,
    CASE WHEN i % 4 = 0 THEN 'INACTIVE' ELSE 'ACTIVE' END
FROM generate_series(1, 12) i;

-- =========================
-- VEHICLES
-- =========================

-- Initial vehicle
INSERT INTO vehicle (
    id, model, license_plate, number_of_seats,
    driver_id, vehicle_type_id
) VALUES (
             nextval('vehicle_seq'),
             'Toyota Prius',
             'NS-123-AB',
             4,
             (SELECT id FROM app_user WHERE email = 'driver@test.com'),
             (SELECT id FROM vehicle_type WHERE type_name = 'Standard')
         );

-- Add services to initial vehicle
INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT
    (SELECT id FROM vehicle WHERE license_plate = 'NS-123-AB'),
    id
FROM additional_service
WHERE name IN ('WiFi', 'Pet friendly', 'Baby seat');

-- Generate vehicles for all drivers
INSERT INTO vehicle (
    id, model, license_plate, number_of_seats,
    driver_id, vehicle_type_id
)
SELECT
    nextval('vehicle_seq'),
    CASE
        WHEN u.rn % 5 = 1 THEN 'Honda Accord'
        WHEN u.rn % 5 = 2 THEN 'Tesla Model 3'
        WHEN u.rn % 5 = 3 THEN 'Mercedes E-Class'
        WHEN u.rn % 5 = 4 THEN 'Ford Transit'
        ELSE 'BMW 5 Series'
        END,
    'NS-' || (200 + u.rn) || '-DR',
    CASE WHEN vt.type_name = 'Van' THEN 7 ELSE 4 END,
    u.id,
    vt.id
FROM (
         SELECT id, ROW_NUMBER() OVER (ORDER BY id) rn
         FROM app_user
         WHERE role = 'DRIVER' AND email != 'driver@test.com'
     ) u
         JOIN (
    SELECT id, type_name, ROW_NUMBER() OVER (ORDER BY id) rn
    FROM vehicle_type
) vt ON (u.rn - 1) % (SELECT COUNT(*) FROM vehicle_type) = vt.rn - 1;

-- Add services to generated vehicles (varied distribution)
INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT DISTINCT
    v.id,
    s.id
FROM vehicle v
         CROSS JOIN additional_service s
WHERE v.license_plate LIKE 'NS-%-DR'
  AND (
    (v.id % 3 = 0 AND s.name IN ('WiFi', 'Pet friendly'))
        OR (v.id % 3 = 1 AND s.name IN ('Baby seat', 'WiFi'))
        OR (v.id % 3 = 2 AND s.name IN ('Pet friendly', 'Baby seat', 'WiFi'))
    );

-- =========================
-- DRIVER ACTIVITIES
-- =========================

INSERT INTO driver_activity (id, start_time, end_time, driver_id)
SELECT
    nextval('driver_activity_seq'),
    NOW() - (random() * interval '30 days'),
    NOW() - (random() * interval '20 days'),
    d.id
FROM (
         SELECT id FROM app_user WHERE role = 'DRIVER' LIMIT 8
     ) d;

-- =========================
-- RIDES (33 rides - each passenger gets their own ride)
-- =========================
-- =========================
-- RIDES (Corrected Column Alignment)
-- =========================

INSERT INTO ride (id, start_time, end_time, scheduled_time, created_at, status, path, distance_km, price, total_cost, driver_id, ride_owner_id, vehicle_type_id, route_id) VALUES
-- Ride 1 - 15: Finished Rides
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '25 minutes', NULL, NOW() - interval '2 days' - interval '15 minutes', 'FINISHED', 'LINESTRING(19.838263 45.236663, 19.851894 45.246357)', 3.2, 250.00, 280.00, (SELECT id FROM app_user WHERE email = 'driver@test.com'), (SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 0)),
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '30 minutes', NULL, NOW() - interval '4 days' - interval '20 minutes', 'FINISHED', 'LINESTRING(19.842198 45.254847, 19.836944 45.251667)', 2.8, 220.00, 250.00, (SELECT id FROM app_user WHERE email = 'driver1@test.com'), (SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 1)),
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '35 minutes', NULL, NOW() - interval '4 days' - interval '10 minutes', 'FINISHED', 'LINESTRING(19.823217 45.258097, 19.823813 45.252606)', 4.1, 320.00, 360.00, (SELECT id FROM app_user WHERE email = 'driver2@test.com'), (SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Premium'), (SELECT id FROM route LIMIT 1 OFFSET 2)),
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '20 minutes', NULL, NOW() - interval '3 days' - interval '25 minutes', 'FINISHED', 'LINESTRING(19.829628 45.265291, 19.845087 45.259172)', 3.5, 270.00, 300.00, (SELECT id FROM app_user WHERE email = 'driver3@test.com'), (SELECT id FROM app_user WHERE email = 'customer3@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 3)),
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '40 minutes', NULL, NOW() - interval '3 days' - interval '30 minutes', 'FINISHED', 'LINESTRING(19.793551 45.244466, 19.823813 45.252606, 19.842198 45.254847)', 5.2, 380.00, 420.00, (SELECT id FROM app_user WHERE email = 'driver@test.com'), (SELECT id FROM app_user WHERE email = 'customer4@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 5)),
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '28 minutes', NULL, NOW() - interval '2 days' - interval '18 minutes', 'FINISHED', 'LINESTRING(19.851894 45.246357, 19.847222 45.248611, 19.845556 45.255833)', 3.8, 290.00, 330.00, (SELECT id FROM app_user WHERE email = 'driver1@test.com'), (SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Electric'), (SELECT id FROM route LIMIT 1 OFFSET 6)),
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '33 minutes', NULL, NOW() - interval '2 days' - interval '22 minutes', 'FINISHED', 'LINESTRING(19.836944 45.251667, 19.835000 45.250556, 19.838263 45.236663)', 4.3, 340.00, 380.00, (SELECT id FROM app_user WHERE email = 'driver2@test.com'), (SELECT id FROM app_user WHERE email = 'customer6@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 7)),
(nextval('ride_seq'), NOW() - interval '1 day', NOW() - interval '1 day' + interval '22 minutes', NULL, NOW() - interval '1 day' - interval '12 minutes', 'FINISHED', 'LINESTRING(19.791667 45.241667, 19.825556 45.247778, 19.832222 45.249444, 19.836944 45.251667)', 6.5, 450.00, 500.00, (SELECT id FROM app_user WHERE email = 'driver3@test.com'), (SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Van'), (SELECT id FROM route LIMIT 1 OFFSET 8)),
(nextval('ride_seq'), NOW() - interval '1 day', NOW() - interval '1 day' + interval '38 minutes', NULL, NOW() - interval '1 day' - interval '28 minutes', 'FINISHED', 'LINESTRING(19.833333 45.267222, 19.829628 45.265291, 19.823813 45.252606, 19.842198 45.254847, 19.851894 45.246357)', 7.8, 520.00, 580.00, (SELECT id FROM app_user WHERE email = 'driver@test.com'), (SELECT id FROM app_user WHERE email = 'customer8@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Premium'), (SELECT id FROM route LIMIT 1 OFFSET 9)),
(nextval('ride_seq'), NOW() - interval '12 hours', NOW() - interval '12 hours' + interval '26 minutes', NULL, NOW() - interval '12 hours' - interval '16 minutes', 'FINISHED', 'LINESTRING(19.841111 45.238889, 19.838263 45.236663, 19.836944 45.251667, 19.845087 45.259172, 19.829628 45.265291)', 8.2, 550.00, 620.00, (SELECT id FROM app_user WHERE email = 'driver1@test.com'), (SELECT id FROM app_user WHERE email = 'customer9@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 10)),
(nextval('ride_seq'), NOW() - interval '10 hours', NOW() - interval '10 hours' + interval '31 minutes', NULL, NOW() - interval '10 hours' - interval '20 minutes', 'FINISHED', 'LINESTRING(19.851111 45.262778, 19.835000 45.270000, 19.833333 45.267222, 19.847222 45.248611)', 6.9, 480.00, 540.00, (SELECT id FROM app_user WHERE email = 'driver2@test.com'), (SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Electric'), (SELECT id FROM route LIMIT 1 OFFSET 11)),
(nextval('ride_seq'), NOW() - interval '8 hours', NOW() - interval '8 hours' + interval '24 minutes', NULL, NOW() - interval '8 hours' - interval '14 minutes', 'FINISHED', 'LINESTRING(19.838263 45.236663, 19.851894 45.246357)', 3.1, 240.00, 270.00, (SELECT id FROM app_user WHERE email = 'driver3@test.com'), (SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 0)),
(nextval('ride_seq'), NOW() - interval '6 hours', NOW() - interval '6 hours' + interval '29 minutes', NULL, NOW() - interval '6 hours' - interval '19 minutes', 'FINISHED', 'LINESTRING(19.842198 45.254847, 19.836944 45.251667)', 2.9, 230.00, 260.00, (SELECT id FROM app_user WHERE email = 'driver@test.com'), (SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 1)),
(nextval('ride_seq'), NOW() - interval '4 hours', NOW() - interval '4 hours' + interval '36 minutes', NULL, NOW() - interval '4 hours' - interval '26 minutes', 'FINISHED', 'LINESTRING(19.793551 45.244466, 19.823217 45.258097, 19.838263 45.236663, 19.842198 45.254847)', 7.1, 490.00, 550.00, (SELECT id FROM app_user WHERE email = 'driver1@test.com'), (SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Van'), (SELECT id FROM route LIMIT 1 OFFSET 3)),
(nextval('ride_seq'), NOW() - interval '2 hours', NOW() - interval '2 hours' + interval '27 minutes', NULL, NOW() - interval '2 hours' - interval '17 minutes', 'FINISHED', 'LINESTRING(19.851894 45.246357, 19.827778 45.253333, 19.793551 45.244466, 19.823217 45.258097)', 5.8, 410.00, 460.00, (SELECT id FROM app_user WHERE email = 'driver2@test.com'), (SELECT id FROM app_user WHERE email = 'customer3@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Premium'), (SELECT id FROM route LIMIT 1 OFFSET 3)),

-- Ride 16 - 19: Active Rides
(nextval('ride_seq'), NOW() - interval '18 minutes', NULL, NULL, NOW() - interval '25 minutes', 'ACTIVE', 'LINESTRING(19.838263 45.236663, 19.841111 45.238889)', 2.3, 180.00, 200.00, (SELECT id FROM app_user WHERE email = 'driver3@test.com'), (SELECT id FROM app_user WHERE email = 'customer4@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 0)),
(nextval('ride_seq'), NOW() - interval '12 minutes', NULL, NULL, NOW() - interval '20 minutes', 'ACTIVE', 'LINESTRING(19.829628 45.265291, 19.835000 45.270000)', 1.8, 150.00, 170.00, (SELECT id FROM app_user WHERE email = 'driver@test.com'), (SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Electric'), (SELECT id FROM route LIMIT 1 OFFSET 4)),
(nextval('ride_seq'), NOW() - interval '8 minutes', NULL, NULL, NOW() - interval '15 minutes', 'ACTIVE', 'LINESTRING(19.823813 45.252606, 19.827778 45.253333, 19.832222 45.249444)', 3.1, 240.00, 270.00, (SELECT id FROM app_user WHERE email = 'driver1@test.com'), (SELECT id FROM app_user WHERE email = 'customer6@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 7)),
(nextval('ride_seq'), NOW() - interval '5 minutes', NULL, NULL, NOW() - interval '10 minutes', 'ACTIVE', 'LINESTRING(19.845087 45.259172, 19.845556 45.255833)', 1.5, 130.00, 150.00, (SELECT id FROM app_user WHERE email = 'driver2@test.com'), (SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 3)),

-- Ride 20 - 22: Accepted Rides
(nextval('ride_seq'), NULL, NULL, NOW() + interval '5 minutes', NOW() - interval '12 minutes', 'ACCEPTED', NULL, 4.5, 350.00, 390.00, (SELECT id FROM app_user WHERE email = 'driver3@test.com'), (SELECT id FROM app_user WHERE email = 'customer8@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Premium'), (SELECT id FROM route LIMIT 1 OFFSET 8)),
(nextval('ride_seq'), NULL, NULL, NOW() + interval '3 minutes', NOW() - interval '8 minutes', 'ACCEPTED', NULL, 5.2, 380.00, 430.00, (SELECT id FROM app_user WHERE email = 'driver@test.com'), (SELECT id FROM app_user WHERE email = 'customer9@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 9)),
(nextval('ride_seq'), NULL, NULL, NOW() + interval '3 minutes', NOW() - interval '15 minutes', 'ACCEPTED', NULL, 6.8, 470.00, 530.00, (SELECT id FROM app_user WHERE email = 'driver1@test.com'), (SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Van'), (SELECT id FROM route LIMIT 1 OFFSET 2)),

-- Ride 23 - 25: Pending Rides
(nextval('ride_seq'), NULL, NULL, NOW() + interval '45 minutes', NOW() - interval '5 minutes', 'PENDING', NULL, 3.8, 290.00, 330.00, NULL, (SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 2)),
(nextval('ride_seq'), NULL, NULL, NOW() + interval '1 hour 30 minutes', NOW() - interval '3 minutes', 'PENDING', NULL, 4.2, 320.00, 360.00, NULL, (SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Electric'), (SELECT id FROM route LIMIT 1 OFFSET 5)),
(nextval('ride_seq'), NULL, NULL, NOW() + interval '2 hours 15 minutes', NOW() - interval '7 minutes', 'PENDING', NULL, 5.5, 400.00, 450.00, NULL, (SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 6)),

-- Ride 26 - 30: Guest Rides
(nextval('ride_seq'), NOW() - interval '2 days', NOW() - interval '2 days' + interval '32 minutes', NULL, NOW() - interval '2 days' - interval '10 minutes', 'FINISHED', 'LINESTRING(19.836944 45.251667, 19.842198 45.254847, 19.851894 45.246357)', 4.7, 360.00, 400.00, (SELECT id FROM app_user WHERE email = 'driver2@test.com'), (SELECT id FROM app_user WHERE email = 'customer3@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 4)),
(nextval('ride_seq'), NOW() - interval '1 day', NOW() - interval '1 day' + interval '19 minutes', NULL, NOW() - interval '1 day' - interval '8 minutes', 'FINISHED', 'LINESTRING(19.823813 45.252606, 19.829628 45.265291)', 2.6, 210.00, 240.00, (SELECT id FROM app_user WHERE email = 'driver3@test.com'), (SELECT id FROM app_user WHERE email = 'customer4@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Electric'), (SELECT id FROM route LIMIT 1 OFFSET 3)),
(nextval('ride_seq'), NOW() - interval '22 minutes', NULL, NULL, NOW() - interval '30 minutes', 'ACTIVE', 'LINESTRING(19.841111 45.238889, 19.827778 45.253333)', 2.1, 170.00, 190.00, (SELECT id FROM app_user WHERE email = 'driver@test.com'), (SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 1)),
(nextval('ride_seq'), NULL, NULL, NOW() + interval '50 minutes', NOW() - interval '6 minutes', 'ACCEPTED', NULL, 3.3, 260.00, 290.00, (SELECT id FROM app_user WHERE email = 'driver1@test.com'), (SELECT id FROM app_user WHERE email = 'customer6@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 10)),
(nextval('ride_seq'), NULL, NULL, NOW() + interval '1 hour 10 minutes', NOW() - interval '4 minutes', 'PENDING', NULL, 4.0, 310.00, 350.00, NULL, (SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Premium'), (SELECT id FROM route LIMIT 1 OFFSET 11)),

-- FIXED: ADDITIONAL RIDES (3) - Column counts now match
-- Ride 31: Cancelled by customer
(nextval('ride_seq'), NULL, NULL, NOW() - interval '2 hours', NOW() - interval '3 hours', 'CANCELLED', NULL, 3.5, 270.00, 300.00, NULL, (SELECT id FROM app_user WHERE email = 'customer8@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Standard'), (SELECT id FROM route LIMIT 1 OFFSET 2)),

-- Ride 32: Cancelled - no driver
(nextval('ride_seq'), NULL, NULL, NOW() - interval '5 hours', NOW() - interval '6 hours', 'CANCELLED', NULL, 4.8, 370.00, 420.00, NULL, (SELECT id FROM app_user WHERE email = 'customer9@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Comfort'), (SELECT id FROM route LIMIT 1 OFFSET 7)),

-- Ride 33: Interrupted
(nextval('ride_seq'), NOW() - interval '2 hours', NOW() - interval '2 hours 25 minutes', NULL, NOW() - interval '2 hours 10 minutes', 'INTERRUPTED', 'LINESTRING(19.851894 45.246357, 19.845556 45.255833)', 1.9, 160.00, 180.00, (SELECT id FROM app_user WHERE email = 'driver2@test.com'), (SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM vehicle_type WHERE type_name = 'Electric'), (SELECT id FROM route LIMIT 1 OFFSET 4));
-- =========================
-- RIDE ADDITIONAL SERVICES
-- =========================

-- Add services to rides (some rides have additional services)
INSERT INTO ride_additional_services (ride_id, additional_services_id)
SELECT DISTINCT
    r.id,
    s.id
FROM ride r
         CROSS JOIN additional_service s
WHERE r.status IN ('FINISHED', 'ACTIVE', 'ACCEPTED')
  AND random() < 0.4; -- 40% chance for each service

-- =========================
-- PASSENGERS (30 total - each linked to their specific ride)
-- =========================

-- Passengers WITH ratings on FINISHED rides (15 passengers)
INSERT INTO passenger (id, email, access_token, user_id, ride_id, driver_rating, vehicle_rating, comment) VALUES
                                                                                                              (nextval('passenger_seq'), 'customer@test.com', 'token001', (SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 0), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer1@test.com', 'token002', (SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 1), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer2@test.com', 'token003', (SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 2), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer3@test.com', 'token004', (SELECT id FROM app_user WHERE email = 'customer3@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 3), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer4@test.com', 'token005', (SELECT id FROM app_user WHERE email = 'customer4@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 4), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer5@test.com', 'token006', (SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 5), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer6@test.com', 'token007', (SELECT id FROM app_user WHERE email = 'customer6@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 6), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer7@test.com', 'token008', (SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 7), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer8@test.com', 'token009', (SELECT id FROM app_user WHERE email = 'customer8@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 8), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer9@test.com', 'token010', (SELECT id FROM app_user WHERE email = 'customer9@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 9), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer10@test.com', 'token011', (SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 10), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer@test.com', 'token012', (SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 11), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer1@test.com', 'token013', (SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 12), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer2@test.com', 'token014', (SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 13), NULL, NULL, NULL),
(nextval('passenger_seq'), 'customer3@test.com', 'token015', (SELECT id FROM app_user WHERE email = 'customer3@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 14), NULL, NULL, NULL);

-- Passengers WITHOUT ratings on ACTIVE rides (4 passengers)
INSERT INTO passenger (id, email, access_token, user_id, ride_id) VALUES
    (nextval('passenger_seq'), 'customer4@test.com', 'token016', (SELECT id FROM app_user WHERE email = 'customer4@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 15)),
(nextval('passenger_seq'), 'customer5@test.com', 'token017', (SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 16)),
(nextval('passenger_seq'), 'customer6@test.com', 'token018', (SELECT id FROM app_user WHERE email = 'customer6@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 17)),
(nextval('passenger_seq'), 'customer7@test.com', 'token019', (SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 18));

-- Passengers WITHOUT ratings on ACCEPTED rides (3 passengers)
INSERT INTO passenger (id, email, access_token, user_id, ride_id) VALUES
    (nextval('passenger_seq'), 'customer8@test.com', 'token020', (SELECT id FROM app_user WHERE email = 'customer8@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 19)),
(nextval('passenger_seq'), 'customer9@test.com', 'token021', (SELECT id FROM app_user WHERE email = 'customer9@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 20)),
(nextval('passenger_seq'), 'customer10@test.com', 'token022', (SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 21));

-- Passengers WITHOUT ratings on PENDING rides (3 passengers)
INSERT INTO passenger (id, email, access_token, user_id, ride_id) VALUES
    (nextval('passenger_seq'), 'customer@test.com', 'token023', (SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 22)),
(nextval('passenger_seq'), 'customer1@test.com', 'token024', (SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 23)),
(nextval('passenger_seq'), 'customer2@test.com', 'token025', (SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 24));
-- Guest passengers (no user_id) - 5 passengers
INSERT INTO passenger (id, email, access_token, user_id, ride_id) VALUES
-- Passenger 26: Ride 26 (FINISHED - guest)
(nextval('passenger_seq'), 'guest1@example.com', 'guesttoken001', NULL, (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 25)),
-- Passenger 27: Ride 27 (FINISHED - guest)
(nextval('passenger_seq'), 'guest2@example.com', 'guesttoken002', NULL, (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 26)),
-- Passenger 28: Ride 28 (ACTIVE - guest)
(nextval('passenger_seq'), 'visitor@example.com', 'guesttoken003', NULL, (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 27)),
-- Passenger 29: Ride 29 (ACCEPTED - guest)
(nextval('passenger_seq'), 'temp.user@example.com', 'guesttoken004', NULL, (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 28)),
-- Passenger 30: Ride 30 (PENDING - guest)
(nextval('passenger_seq'), 'anonymous@example.com', 'guesttoken005', NULL, (SELECT id FROM ride ORDER BY id LIMIT 1 OFFSET 29));

-- =========================
-- FAVORITE ROUTES FOR CUSTOMERS
-- =========================

-- Add favorite routes for customers (many-to-many relationship)
INSERT INTO app_user_favorite_routes (customer_id, favorite_routes_id) VALUES
-- customer@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 0)),
((SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 1)),
((SELECT id FROM app_user WHERE email = 'customer@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 4)),

-- customer1@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 2)),
((SELECT id FROM app_user WHERE email = 'customer1@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 5)),

-- customer2@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 0)),
((SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 3)),
((SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 6)),
((SELECT id FROM app_user WHERE email = 'customer2@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 9)),

-- customer3@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer3@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 1)),
((SELECT id FROM app_user WHERE email = 'customer3@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 7)),

-- customer4@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer4@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 8)),

-- customer5@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 4)),
((SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 10)),
((SELECT id FROM app_user WHERE email = 'customer5@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 11)),

-- customer6@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer6@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 2)),
((SELECT id FROM app_user WHERE email = 'customer6@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 12)),

-- customer7@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 0)),
((SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 5)),
((SELECT id FROM app_user WHERE email = 'customer7@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 13)),

-- customer8@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer8@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 14)),

-- customer9@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer9@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 3)),
((SELECT id FROM app_user WHERE email = 'customer9@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 9)),

-- customer10@test.com favorites
((SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 6)),
((SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 7)),
((SELECT id FROM app_user WHERE email = 'customer10@test.com'), (SELECT id FROM route LIMIT 1 OFFSET 8));

-- =========================
-- NOTIFICATIONS
-- =========================

INSERT INTO notification (
    id, app_user_id, timestamp, title, message, read, data
)
SELECT
    nextval('notification_seq'),
    u.id,
    NOW() - (random() * interval '7 days'),
    CASE
        WHEN u.role = 'DRIVER' THEN 'New ride request'
        WHEN u.role = 'CUSTOMER' THEN 'Ride completed'
        ELSE 'System notification'
        END,
    CASE
        WHEN u.role = 'DRIVER' THEN 'You have a new ride request in your area'
        WHEN u.role = 'CUSTOMER' THEN 'Your ride has been completed. Please rate your experience.'
        ELSE 'Welcome to the platform!'
        END,
    random() < 0.6,
    NULL
FROM (
         SELECT id, role FROM app_user WHERE role IN ('DRIVER', 'CUSTOMER') ORDER BY random() LIMIT 20
     ) u;
