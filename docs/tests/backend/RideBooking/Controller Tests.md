# Unit Test Scenarios - RideInteractionController (createRide Endpoint)

This document outlines the test scenarios for the `createRide` endpoint in `RideInteractionController`. This endpoint handles HTTP POST requests to create new ride bookings and is part of the ride booking feature across all layers.

## Table of Contents

| Test Case | Title |
|-----------|-------------|
| [Test Case 1](#test-case-1) | Create ride - successful with authenticated user |
| [Test Case 2](#test-case-2) | Create ride - unauthorized (no authentication) |
| [Test Case 3](#test-case-3) | Create ride - user is blocked (ForbiddenException) |
| [Test Case 4](#test-case-4) | Create ride - invalid scheduled time (BadRequestException) |
| [Test Case 5](#test-case-5) | Create ride - vehicle type not found (ResourceNotFoundException) |
| [Test Case 6](#test-case-6) | Create ride - no suitable driver found (ResourceNotFoundException) |
| [Test Case 7](#test-case-7) | Create ride - with scheduled time (pending status) |
| [Test Case 8](#test-case-8) | Create ride - immediate ride (accepted status with driver) |

---

## Test Case 1

**Title:** Create ride - successful with authenticated user

**Description**  
Verify that an authenticated customer can successfully create a ride booking and receive the correct response with HTTP 201 Created status.

**Assumptions / Preconditions**  
- User is authenticated with valid JWT token
- User is a Customer
- All required data in request is valid
- RideBookingService successfully creates the ride

**Setup / Arrange**  
- Mock authenticated Customer user
- Create valid `RideBookingParametersDTO` request body
- Mock `rideBookingService.bookRide()` to return successful `NewRideDTO`

**Action / Act**  
- Send POST request to `/api/v1/rides` with valid request body and authentication

**Expected Outcome / Assert**  
- Response status is HTTP 201 Created
- Response body contains `NewRideDTO` with ride ID, status, and estimated distance
- `rideBookingService.bookRide()` is called once with correct user ID and request body
- `authUtils.getCurrentUser()` is called once

**Notes / Additional Info**  
This is the happy path for ride creation.

---

## Test Case 2

**Title:** Create ride - unauthorized (no authentication)

**Description**  
Verify that unauthenticated requests are rejected with HTTP 401 Unauthorized status.

**Assumptions / Preconditions**  
- No authentication token is provided
- `authUtils.getCurrentUser()` returns null

**Setup / Arrange**  
- Mock `authUtils.getCurrentUser()` to return null
- Create valid `RideBookingParametersDTO` request body

**Action / Act**  
- Send POST request to `/api/v1/rides` without authentication

**Expected Outcome / Assert**  
- Response status is HTTP 401 Unauthorized
- Response body contains error message: `{"error": "Unauthorized"}`
- `rideBookingService.bookRide()` is never called
- `authUtils.getCurrentUser()` is called once

**Notes / Additional Info**  
Security test to ensure endpoint is protected.

---

## Test Case 3

**Title:** Create ride - user is blocked (ForbiddenException)

**Description**  
Verify that when a blocked user attempts to create a ride, the service throws ForbiddenException and returns HTTP 403 Forbidden.

**Assumptions / Preconditions**  
- User is authenticated
- User's account is blocked
- `rideBookingService.bookRide()` throws `ForbiddenException`

**Setup / Arrange**  
- Mock authenticated Customer user
- Create valid `RideBookingParametersDTO` request body
- Mock `rideBookingService.bookRide()` to throw `ForbiddenException` with message

**Action / Act**  
- Send POST request to `/api/v1/rides` with valid request body

**Expected Outcome / Assert**  
- Response status is HTTP 403 Forbidden
- Response body contains error details with block reason
- `rideBookingService.bookRide()` is called once
- Exception is properly handled by exception handler

**Notes / Additional Info**  
Requires global exception handler to be configured.

---

## Test Case 4

**Title:** Create ride - invalid scheduled time (BadRequestException)

**Description**  
Verify that when invalid scheduled time is provided (e.g., in the past or too far in future), the service throws BadRequestException and returns HTTP 400.

**Assumptions / Preconditions**  
- User is authenticated
- Request contains invalid scheduled time
- `rideBookingService.bookRide()` throws `BadRequestException`

**Setup / Arrange**  
- Mock authenticated Customer user
- Create `RideBookingParametersDTO` with invalid scheduled time
- Mock `rideBookingService.bookRide()` to throw `BadRequestException`

**Action / Act**  
- Send POST request to `/api/v1/rides` with invalid scheduled time

**Expected Outcome / Assert**  
- Response status is HTTP 400 Bad Request
- Response body contains error message about invalid scheduled time
- `rideBookingService.bookRide()` is called once
- Exception is properly handled by exception handler

**Notes / Additional Info**  
Validates business rule enforcement at controller layer.

---

## Test Case 5

**Title:** Create ride - vehicle type not found (ResourceNotFoundException)

**Description**  
Verify that when a non-existent vehicle type ID is provided, the service throws ResourceNotFoundException and returns HTTP 404.

**Assumptions / Preconditions**  
- User is authenticated
- Request contains non-existent vehicle type ID
- `rideBookingService.bookRide()` throws `ResourceNotFoundException`

**Setup / Arrange**  
- Mock authenticated Customer user
- Create `RideBookingParametersDTO` with non-existent vehicle type ID
- Mock `rideBookingService.bookRide()` to throw `ResourceNotFoundException`

**Action / Act**  
- Send POST request to `/api/v1/rides`

**Expected Outcome / Assert**  
- Response status is HTTP 404 Not Found
- Response body contains error message about vehicle type not found
- `rideBookingService.bookRide()` is called once
- Exception is properly handled by exception handler

**Notes / Additional Info**  
Tests resource validation at service layer.

---

## Test Case 6

**Title:** Create ride - no suitable driver found (ResourceNotFoundException)

**Description**  
Verify that when no suitable driver is available for an immediate ride, the service throws ResourceNotFoundException and returns HTTP 404.

**Assumptions / Preconditions**  
- User is authenticated
- Request is for immediate ride (no scheduled time)
- No drivers are available
- `rideBookingService.bookRide()` throws `ResourceNotFoundException`

**Setup / Arrange**  
- Mock authenticated Customer user
- Create `RideBookingParametersDTO` without scheduled time
- Mock `rideBookingService.bookRide()` to throw `ResourceNotFoundException` with "No suitable driver found" message

**Action / Act**  
- Send POST request to `/api/v1/rides`

**Expected Outcome / Assert**  
- Response status is HTTP 404 Not Found
- Response body contains error message about no driver found
- `rideBookingService.bookRide()` is called once
- Exception is properly handled by exception handler

**Notes / Additional Info**  
Critical business scenario - user should be informed when no drivers are available.

---

## Test Case 7

**Title:** Create ride - with scheduled time (pending status)

**Description**  
Verify that creating a scheduled ride returns PENDING status without driver assignment.

**Assumptions / Preconditions**  
- User is authenticated
- Request contains valid future scheduled time
- RideBookingService creates ride with PENDING status

**Setup / Arrange**  
- Mock authenticated Customer user
- Create `RideBookingParametersDTO` with valid future scheduled time
- Mock `rideBookingService.bookRide()` to return `NewRideDTO` with PENDING status and no estimated distance

**Action / Act**  
- Send POST request to `/api/v1/rides` with scheduled time

**Expected Outcome / Assert**  
- Response status is HTTP 201 Created
- Response body contains `NewRideDTO` with:
  - Ride ID
  - Status = "PENDING"
  - Estimated distance = null (no driver assigned yet)
- `rideBookingService.bookRide()` is called once with correct parameters

**Notes / Additional Info**  
Verifies correct handling of scheduled rides vs immediate rides.

---

## Test Case 8

**Title:** Create ride - immediate ride (accepted status with driver)

**Description**  
Verify that creating an immediate ride returns ACCEPTED status with driver assignment and estimated distance.

**Assumptions / Preconditions**  
- User is authenticated
- Request does not contain scheduled time (immediate ride)
- Driver is found and assigned
- RideBookingService creates ride with ACCEPTED status

**Setup / Arrange**  
- Mock authenticated Customer user
- Create `RideBookingParametersDTO` without scheduled time
- Mock `rideBookingService.bookRide()` to return `NewRideDTO` with ACCEPTED status and estimated distance

**Action / Act**  
- Send POST request to `/api/v1/rides` without scheduled time

**Expected Outcome / Assert**  
- Response status is HTTP 201 Created
- Response body contains `NewRideDTO` with:
  - Ride ID
  - Status = "ACCEPTED"
  - Estimated distance (populated value)
- `rideBookingService.bookRide()` is called once with correct parameters

**Notes / Additional Info**  
Verifies immediate ride booking with driver assignment.

---

## Test Execution Notes

### Test Type
These are **Controller Layer / Web Layer** tests using `@WebMvcTest` annotation.

### Key Testing Aspects
- HTTP request/response handling
- Authentication/Authorization
- Request body deserialization
- Response serialization
- HTTP status codes
- Exception handling via `@ControllerAdvice`

### Dependencies Mocked
- `RideBookingService`
- `AuthUtils`
- Other dependencies for the filter chain and controller context