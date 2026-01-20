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

INSERT INTO route (id, geo_hash)
VALUES
    (nextval('route_seq'), 'u2n177jbv'),
    (nextval('route_seq'), 'u2n17szet'),
    (nextval('route_seq'), 'u2n17w6ts'),
    (nextval('route_seq'), 'u2n16t6mn');
