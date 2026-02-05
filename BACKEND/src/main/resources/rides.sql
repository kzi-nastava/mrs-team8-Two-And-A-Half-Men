-- ===================================================================
-- SQL Script to Insert Test Rides for Report Testing
-- Period: 2026-01-01 to 2026-02-04
-- ===================================================================
-- This script creates realistic test data for ride reporting with:
-- - Multiple drivers (IDs: 1201, 1251, 1301, 1351, 1401)
-- - Multiple customers (IDs: 701, 751, 801, 851, 901)
-- - COMPLETED rides with varying dates, distances, and prices
-- - Distributed across the date range
-- ===================================================================

BEGIN;

-- First, let's ensure ride_seq is at a safe value
SELECT setval('public.ride_seq', 1000, false);

-- First, let's ensure passenger_seq is at a safe value
SELECT setval('public.passenger_seq', 1000, false);

-- ===================================================================
-- JANUARY 2026 RIDES
-- ===================================================================

-- Week 1: January 1-7, 2026
-- Driver 1201 (Driver1) - 3 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1001, NULL, '2026-01-02 08:15:00', '2026-01-02 08:45:00', null, 350.00, '2026-01-02 08:00:00', '2026-01-02 08:15:00', 'FINISHED', 385.00, 1201, 701, 1, 1, 12.5),
    (1002, NULL, '2026-01-03 14:20:00', '2026-01-03 14:55:00', null, 420.00, '2026-01-03 14:00:00', '2026-01-03 14:20:00', 'FINISHED', 462.00, 1201, 751, 51, 1, 15.8),
    (1003, NULL, '2026-01-05 09:10:00', '2026-01-05 09:35:00', null, 280.00, '2026-01-05 09:00:00', '2026-01-05 09:10:00', 'FINISHED', 308.00, 1201, 801, 1, 1, 9.2);

-- Driver 1251 (Driver2) - 4 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1004, NULL, '2026-01-01 10:30:00', '2026-01-01 11:15:00', null, 550.00, '2026-01-01 10:00:00', '2026-01-01 10:30:00', 'FINISHED', 605.00, 1251, 851, 51, 51, 22.3),
    (1005, NULL, '2026-01-04 16:45:00', '2026-01-04 17:20:00', null, 380.00, '2026-01-04 16:30:00', '2026-01-04 16:45:00', 'FINISHED', 418.00, 1251, 901, 1, 51, 14.5),
    (1006, NULL, '2026-01-06 11:00:00', '2026-01-06 11:25:00', null, 320.00, '2026-01-06 10:45:00', '2026-01-06 11:00:00', 'FINISHED', 352.00, 1251, 701, 1, 51, 11.8),
    (1007, NULL, '2026-01-07 19:30:00', '2026-01-07 20:10:00', null, 480.00, '2026-01-07 19:00:00', '2026-01-07 19:30:00', 'FINISHED', 528.00, 1251, 751, 51, 51, 18.9);

-- Week 2: January 8-14, 2026
-- Driver 1301 (Driver3) - 5 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1008, NULL, '2026-01-08 07:45:00', '2026-01-08 08:20:00', null, 410.00, '2026-01-08 07:30:00', '2026-01-08 07:45:00', 'FINISHED', 451.00, 1301, 801, 1, 101, 16.2),
    (1009, NULL, '2026-01-09 12:15:00', '2026-01-09 12:45:00', null, 340.00, '2026-01-09 12:00:00', '2026-01-09 12:15:00', 'FINISHED', 374.00, 1301, 851, 1, 101, 13.1),
    (1010, NULL, '2026-01-11 15:30:00', '2026-01-11 16:05:00', null, 450.00, '2026-01-11 15:00:00', '2026-01-11 15:30:00', 'FINISHED', 495.00, 1301, 901, 51, 101, 17.5),
    (1011, NULL, '2026-01-12 10:00:00', '2026-01-12 10:40:00', null, 490.00, '2026-01-12 09:45:00', '2026-01-12 10:00:00', 'FINISHED', 539.00, 1301, 701, 51, 101, 19.3),
    (1012, NULL, '2026-01-14 18:20:00', '2026-01-14 18:50:00', null, 360.00, '2026-01-14 18:00:00', '2026-01-14 18:20:00', 'FINISHED', 396.00, 1301, 751, 1, 101, 14.0);

-- Driver 1351 (Driver4) - 3 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1013, NULL, '2026-01-10 09:30:00', '2026-01-10 10:15:00', null, 530.00, '2026-01-10 09:00:00', '2026-01-10 09:30:00', 'FINISHED', 583.00, 1351, 801, 51, 151, 21.0),
    (1014, NULL, '2026-01-13 14:00:00', '2026-01-13 14:35:00', null, 400.00, '2026-01-13 13:45:00', '2026-01-13 14:00:00', 'FINISHED', 440.00, 1351, 851, 1, 151, 15.6),
    (1015, NULL, '2026-01-14 20:15:00', '2026-01-14 20:50:00', null, 440.00, '2026-01-14 20:00:00', '2026-01-14 20:15:00', 'FINISHED', 484.00, 1351, 901, 51, 151, 17.2);

-- Week 3: January 15-21, 2026
-- Driver 1401 (Driver5) - 6 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1016, NULL, '2026-01-15 08:00:00', '2026-01-15 08:30:00', null, 370.00, '2026-01-15 07:45:00', '2026-01-15 08:00:00', 'FINISHED', 407.00, 1401, 701, 1, 201, 14.3),
    (1017, NULL, '2026-01-16 11:30:00', '2026-01-16 12:10:00', null, 470.00, '2026-01-16 11:00:00', '2026-01-16 11:30:00', 'FINISHED', 517.00, 1401, 751, 51, 201, 18.5),
    (1018, NULL, '2026-01-17 16:00:00', '2026-01-17 16:25:00', null, 310.00, '2026-01-17 15:45:00', '2026-01-17 16:00:00', 'FINISHED', 341.00, 1401, 801, 1, 201, 12.0),
    (1019, NULL, '2026-01-19 10:45:00', '2026-01-19 11:20:00', null, 420.00, '2026-01-19 10:30:00', '2026-01-19 10:45:00', 'FINISHED', 462.00, 1401, 851, 1, 201, 16.5),
    (1020, NULL, '2026-01-20 14:30:00', '2026-01-20 15:05:00', null, 390.00, '2026-01-20 14:15:00', '2026-01-20 14:30:00', 'FINISHED', 429.00, 1401, 901, 51, 201, 15.2),
    (1021, NULL, '2026-01-21 18:45:00', '2026-01-21 19:25:00', null, 480.00, '2026-01-21 18:30:00', '2026-01-21 18:45:00', 'FINISHED', 528.00, 1401, 701, 51, 201, 18.8);

-- Week 4: January 22-28, 2026
-- Driver 1201 (Driver1) - 4 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1022, NULL, '2026-01-22 09:15:00', '2026-01-22 09:50:00', null, 410.00, '2026-01-22 09:00:00', '2026-01-22 09:15:00', 'FINISHED', 451.00, 1201, 751, 1, 1, 16.0),
    (1023, NULL, '2026-01-23 13:30:00', '2026-01-23 14:10:00', null, 460.00, '2026-01-23 13:00:00', '2026-01-23 13:30:00', 'FINISHED', 506.00, 1201, 801, 51, 1, 18.1),
    (1024, NULL, '2026-01-25 11:00:00', '2026-01-25 11:30:00', null, 350.00, '2026-01-25 10:45:00', '2026-01-25 11:00:00', 'FINISHED', 385.00, 1201, 851, 1, 1, 13.6),
    (1025, NULL, '2026-01-27 17:00:00', '2026-01-27 17:40:00', null, 490.00, '2026-01-27 16:45:00', '2026-01-27 17:00:00', 'FINISHED', 539.00, 1201, 901, 51, 1, 19.5);

-- Driver 1251 (Driver2) - 5 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1026, NULL, '2026-01-22 15:20:00', '2026-01-22 15:55:00', null, 420.00, '2026-01-22 15:00:00', '2026-01-22 15:20:00', 'FINISHED', 462.00, 1251, 701, 1, 51, 16.8),
    (1027, NULL, '2026-01-24 10:30:00', '2026-01-24 11:05:00', null, 440.00, '2026-01-24 10:15:00', '2026-01-24 10:30:00', 'FINISHED', 484.00, 1251, 751, 51, 51, 17.4),
    (1028, NULL, '2026-01-26 08:45:00', '2026-01-26 09:20:00', null, 410.00, '2026-01-26 08:30:00', '2026-01-26 08:45:00', 'FINISHED', 451.00, 1251, 801, 1, 51, 16.2),
    (1029, NULL, '2026-01-27 12:00:00', '2026-01-27 12:35:00', null, 430.00, '2026-01-27 11:45:00', '2026-01-27 12:00:00', 'FINISHED', 473.00, 1251, 851, 51, 51, 17.0),
    (1030, NULL, '2026-01-28 19:15:00', '2026-01-28 19:50:00', null, 440.00, '2026-01-28 19:00:00', '2026-01-28 19:15:00', 'FINISHED', 484.00, 1251, 901, 51, 51, 17.3);

-- Week 5: January 29-31, 2026
-- Driver 1301 (Driver3) - 3 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1031, NULL, '2026-01-29 09:00:00', '2026-01-29 09:40:00', null, 480.00, '2026-01-29 08:45:00', '2026-01-29 09:00:00', 'FINISHED', 528.00, 1301, 701, 51, 101, 19.0),
    (1032, NULL, '2026-01-30 14:15:00', '2026-01-30 14:50:00', null, 420.00, '2026-01-30 14:00:00', '2026-01-30 14:15:00', 'FINISHED', 462.00, 1301, 751, 1, 101, 16.6),
    (1033, NULL, '2026-01-31 16:30:00', '2026-01-31 17:10:00', null, 470.00, '2026-01-31 16:15:00', '2026-01-31 16:30:00', 'FINISHED', 517.00, 1301, 801, 51, 101, 18.6);

-- ===================================================================
-- FEBRUARY 2026 RIDES (February 1-4)
-- ===================================================================

-- Driver 1351 (Driver4) - 4 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1034, NULL, '2026-02-01 08:30:00', '2026-02-01 09:10:00', null, 490.00, '2026-02-01 08:15:00', '2026-02-01 08:30:00', 'FINISHED', 539.00, 1351, 851, 51, 151, 19.4),
    (1035, NULL, '2026-02-02 11:45:00', '2026-02-02 12:20:00', null, 430.00, '2026-02-02 11:30:00', '2026-02-02 11:45:00', 'FINISHED', 473.00, 1351, 901, 1, 151, 17.0),
    (1036, NULL, '2026-02-03 15:00:00', '2026-02-03 15:35:00', null, 410.00, '2026-02-03 14:45:00', '2026-02-03 15:00:00', 'FINISHED', 451.00, 1351, 701, 51, 151, 16.3),
    (1037, NULL, '2026-02-04 10:15:00', '2026-02-04 10:50:00', null, 420.00, '2026-02-04 10:00:00', '2026-02-04 10:15:00', 'FINISHED', 462.00, 1351, 751, 1, 151, 16.7);

-- Driver 1401 (Driver5) - 4 rides
INSERT INTO public.ride (id, cancellation_reason, created_at, end_time, path, price, scheduled_time, start_time, status, total_cost, driver_id, ride_owner_id, route_id, vehicle_type_id, distance_km)
VALUES
    (1038, NULL, '2026-02-01 13:20:00', '2026-02-01 13:55:00', null, 430.00, '2026-02-01 13:00:00', '2026-02-01 13:20:00', 'FINISHED', 473.00, 1401, 801, 1, 201, 17.1),
    (1039, NULL, '2026-02-02 17:30:00', '2026-02-02 18:10:00', null, 480.00, '2026-02-02 17:15:00', '2026-02-02 17:30:00', 'FINISHED', 528.00, 1401, 851, 51, 201, 18.9),
    (1040, NULL, '2026-02-03 09:45:00', '2026-02-03 10:15:00', null, 380.00, '2026-02-03 09:30:00', '2026-02-03 09:45:00', 'FINISHED', 418.00, 1401, 901, 1, 201, 15.0),
    (1041, NULL, '2026-02-04 14:00:00', '2026-02-04 14:40:00', null, 470.00, '2026-02-04 13:45:00', '2026-02-04 14:00:00', 'FINISHED', 517.00, 1401, 701, 51, 201, 18.6);

-- ===================================================================
-- INSERT PASSENGERS FOR EACH RIDE
-- ===================================================================
-- Creating passenger records for each completed ride

INSERT INTO public.passenger (id, access_token, comment, driver_rating, email, inconsistency_note, vehicle_rating, ride_id, user_id)
VALUES
-- January rides passengers
(1001, NULL, 'Great ride!', 5, 'customer1@test.com', NULL, 5, 1001, 701),
(1002, NULL, 'Very professional', 5, 'customer2@test.com', NULL, 5, 1002, 751),
(1003, NULL, 'Quick and efficient', 4, 'customer3@test.com', NULL, 4, 1003, 801),
(1004, NULL, 'Excellent service', 5, 'customer4@test.com', NULL, 5, 1004, 851),
(1005, NULL, 'Good driver', 4, 'customer5@test.com', NULL, 4, 1005, 901),
(1006, NULL, 'Comfortable ride', 5, 'customer1@test.com', NULL, 5, 1006, 701),
(1007, NULL, 'On time', 5, 'customer2@test.com', NULL, 5, 1007, 751),
(1008, NULL, 'Safe driving', 5, 'customer3@test.com', NULL, 5, 1008, 801),
(1009, NULL, 'Clean vehicle', 4, 'customer4@test.com', NULL, 4, 1009, 851),
(1010, NULL, 'Pleasant experience', 5, 'customer5@test.com', NULL, 5, 1010, 901),
(1011, NULL, 'Very good', 5, 'customer1@test.com', NULL, 5, 1011, 701),
(1012, NULL, 'Smooth ride', 4, 'customer2@test.com', NULL, 4, 1012, 751),
(1013, NULL, 'Professional', 5, 'customer3@test.com', NULL, 5, 1013, 801),
(1014, NULL, 'Punctual', 5, 'customer4@test.com', NULL, 5, 1014, 851),
(1015, NULL, 'Courteous driver', 5, 'customer5@test.com', NULL, 5, 1015, 901),
(1016, NULL, 'Excellent', 5, 'customer1@test.com', NULL, 5, 1016, 701),
(1017, NULL, 'Very satisfied', 5, 'customer2@test.com', NULL, 5, 1017, 751),
(1018, NULL, 'Good service', 4, 'customer3@test.com', NULL, 4, 1018, 801),
(1019, NULL, 'Comfortable', 5, 'customer4@test.com', NULL, 5, 1019, 851),
(1020, NULL, 'Great experience', 5, 'customer5@test.com', NULL, 5, 1020, 901),
(1021, NULL, 'Perfect ride', 5, 'customer1@test.com', NULL, 5, 1021, 701),
(1022, NULL, 'Very good driver', 5, 'customer2@test.com', NULL, 5, 1022, 751),
(1023, NULL, 'Highly recommend', 5, 'customer3@test.com', NULL, 5, 1023, 801),
(1024, NULL, 'Excellent service', 4, 'customer4@test.com', NULL, 4, 1024, 851),
(1025, NULL, 'Will use again', 5, 'customer5@test.com', NULL, 5, 1025, 901),
(1026, NULL, 'Professional service', 5, 'customer1@test.com', NULL, 5, 1026, 701),
(1027, NULL, 'Clean car', 5, 'customer2@test.com', NULL, 5, 1027, 751),
(1028, NULL, 'Friendly driver', 4, 'customer3@test.com', NULL, 4, 1028, 801),
(1029, NULL, 'On time arrival', 5, 'customer4@test.com', NULL, 5, 1029, 851),
(1030, NULL, 'Safe trip', 5, 'customer5@test.com', NULL, 5, 1030, 901),
(1031, NULL, 'Great ride', 5, 'customer1@test.com', NULL, 5, 1031, 701),
(1032, NULL, 'Comfortable vehicle', 5, 'customer2@test.com', NULL, 5, 1032, 751),
(1033, NULL, 'Smooth journey', 4, 'customer3@test.com', NULL, 4, 1033, 801),
-- February rides passengers
(1034, NULL, 'Excellent driver', 5, 'customer4@test.com', NULL, 5, 1034, 851),
(1035, NULL, 'Very professional', 5, 'customer5@test.com', NULL, 5, 1035, 901),
(1036, NULL, 'Great service', 5, 'customer1@test.com', NULL, 5, 1036, 701),
(1037, NULL, 'Perfect', 4, 'customer2@test.com', NULL, 4, 1037, 751),
(1038, NULL, 'Highly satisfied', 5, 'customer3@test.com', NULL, 5, 1038, 801),
(1039, NULL, 'Amazing ride', 5, 'customer4@test.com', NULL, 5, 1039, 851),
(1040, NULL, 'Good experience', 4, 'customer5@test.com', NULL, 4, 1040, 901),
(1041, NULL, 'Will recommend', 5, 'customer1@test.com', NULL, 5, 1041, 701);


COMMIT;

-- ===================================================================
-- VERIFICATION QUERIES
-- ===================================================================
-- Run these to verify the data was inserted correctly

-- Count total rides by status
SELECT status, COUNT(*) as count
FROM public.ride
WHERE id >= 1001
GROUP BY status
ORDER BY status;

-- Count rides by driver
SELECT
    d.id as driver_id,
    d.first_name || ' ' || d.last_name as driver_name,
    COUNT(r.id) as total_rides,
    SUM(r.price) as total_earned,
    SUM(r.distance_km) as total_distance_km
FROM public.app_user d
         LEFT JOIN public.ride r ON d.id = r.driver_id AND r.id >= 1001 AND r.status = 'FINISHED'
WHERE d.role = 'DRIVER' AND d.id IN (1201, 1251, 1301, 1351, 1401)
GROUP BY d.id, driver_name
ORDER BY d.id;

-- Count rides by date
SELECT
    DATE(end_time) as ride_date,
    COUNT(*) as rides_count,
    SUM(distance_km) as total_distance,
    SUM(price) as total_revenue
FROM public.ride
WHERE id >= 1001 AND status = 'FINISHED'
GROUP BY DATE(end_time)
ORDER BY ride_date;

-- Count rides by customer
SELECT
    c.id as customer_id,
    c.first_name || ' ' || c.last_name as customer_name,
    COUNT(r.id) as total_rides,
    SUM(r.total_cost) as total_spent
FROM public.app_user c
         LEFT JOIN public.ride r ON c.id = r.ride_owner_id AND r.id >= 1001 AND r.status = 'FINISHED'
WHERE c.role = 'CUSTOMER' AND c.id IN (701, 751, 801, 851, 901)
GROUP BY c.id, customer_name
ORDER BY c.id;

-- ===================================================================
-- SUMMARY
-- ===================================================================
-- Total Rides Created: 41 FINISHED rides
-- Date Range: 2026-01-01 to 2026-02-04
-- Drivers: 5 (IDs: 1201, 1251, 1301, 1351, 1401)
-- Customers: 5 (IDs: 701, 751, 801, 851, 901)
--
-- Distribution:
-- - Driver 1201 (Driver1): 7 rides
-- - Driver 1251 (Driver2): 9 rides
-- - Driver 1301 (Driver3): 8 rides
-- - Driver 1351 (Driver4): 7 rides
-- - Driver 1401 (Driver5): 10 rides
--
-- All rides have:
-- - FINISHED status
-- - Realistic timestamps spread across the date range
-- - Distance in kilometers (9.2 - 22.3 km)
-- - Price for driver (280 - 550)
-- - Total cost for passenger (308 - 605)
-- - Associated passenger records
-- ===================================================================