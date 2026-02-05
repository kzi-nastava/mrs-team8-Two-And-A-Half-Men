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
-- USERS (SINGLE_TABLE)
-- =========================

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
    NULL,
    NULL,
    NULL,
    true,
    false
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
    NULL,
    NULL,
    NULL,
    true,
    false
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
    NULL,
    NULL,
    NULL,
    true,
    false
);

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

INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT
    (SELECT id FROM vehicle WHERE license_plate = 'NS-123-AB'),
    id
FROM additional_service
WHERE name IN ('WiFi', 'Pet friendly', 'Baby seat');

INSERT INTO location (id, geo_hash, latitude, longitude, address) VALUES
(nextval('location_seq'), 'u2n177jf9j1j', 45.236663, 19.838263, 'B dom'),
(nextval('location_seq'), 'u2n17szensm0', 45.246357, 19.851894, 'FTN'),
(nextval('location_seq'), 'u2n17w2ssdzj', 45.254847, 19.842198, 'Pozorište'),
(nextval('location_seq'), 'u2n17nfzd2vb', 45.258097, 19.823217, 'Sajam'),
(nextval('location_seq'), 'u2n1e0r3rvf6', 45.265291, 19.829628, 'Železnička'),
(nextval('location_seq'), 'u2n16kt0p5v2', 45.244466, 19.793551, 'JGSP'),
(nextval('location_seq'), 'u2n17jgr834m', 45.252606, 19.823813, 'Medicinski'),
(nextval('location_seq'), 'u2n17x4vb11m', 45.259172, 19.845087, 'Matica Srpska');

INSERT INTO route (id, geo_hash) VALUES
(nextval('route_seq'), 'u2n177jf9j1ju2n17szensm0'),
(nextval('route_seq'), 'u2n1e0r3rvf6u2n17jgr834mu2n17nfzd2vb'),
(nextval('route_seq'), 'u2n17w2ssdzju2n16kt0p5v2u2n17x4vb11mu2n17nfzd2vbu2n17szensm0'),
(nextval('route_seq'), 'u2n16kt0p5v2u2n17nfzd2vbu2n177jf9j1ju2n17w2ssdzj');


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
    true,
    false
FROM generate_series(1, 10) i;

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
    true,
    false
FROM generate_series(1, 10) i;

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
    'ACTIVE'
FROM generate_series(1, 10) i;

INSERT INTO vehicle (
    id, model, license_plate, number_of_seats,
    driver_id, vehicle_type_id
)
SELECT
    nextval('vehicle_seq'),
    'Vehicle Model ' || u.rn,
    'NS-' || (200 + u.rn) || '-DR',
    4,
    u.id,
    vt.id
FROM (
         SELECT id, ROW_NUMBER() OVER () rn
         FROM app_user
         WHERE role = 'DRIVER'
         ORDER BY id DESC
         LIMIT 10
     ) u
         JOIN (
    SELECT id, ROW_NUMBER() OVER () rn
    FROM vehicle_type
) vt ON (u.rn - 1) % (SELECT COUNT(*) FROM vehicle_type) = vt.rn - 1;

INSERT INTO vehicle_additional_services (vehicle_id, additional_services_id)
SELECT
    v.id,
    s.id
FROM vehicle v
         JOIN additional_service s
              ON s.name IN ('WiFi', 'Pet friendly', 'Baby seat')
WHERE v.license_plate LIKE 'NS-%-DR';
