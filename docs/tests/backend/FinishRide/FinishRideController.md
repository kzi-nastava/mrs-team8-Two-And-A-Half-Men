# RideController Unit Tests - finishRide Endpoint

## Overview
Unit tests for the `PATCH /api/v1/rides/{id}/finish` endpoint that verify HTTP request/response handling with mocked service layer.

---

## Table of Contents

| Test Case | Title |
|-----------|-------|
| Test Case 1 | Finish ride - successful with no next ride |
| Test Case 2 | Finish ride - successful with next ride returned |
| Test Case 3 | Finish ride - interrupted flag set to true |
| Test Case 4 | Finish ride - ride not found (ResourceNotFoundException) |
| Test Case 5 | Finish ride - invalid request body (missing interrupted field) |
| Test Case 6 | Finish ride - invalid ID format |
| Test Case 7 | Finish ride - missing content type |
| Test Case 8 | Finish ride - with zero ID |
| Test Case 9 | Finish ride - with negative ID |

---

## Test Case 1

**Title:** Finish ride - successful with no next ride

### Description
Verify that when a ride is finished and no next ride exists, the endpoint returns HTTP 200 with empty body.

### Assumptions / Preconditions
- Valid ride ID provided (ID = 1)
- Service returns null (no next ride in queue)
- Controller does not perform authentication check — security is handled by `@PreAuthorize`

### Setup / Arrange
- Create normal FinishRideDTO (interrupted = false)
- Mock `rideService.finishRide` to return null

### Action / Act
- Send PATCH request to `/api/v1/rides/1/finish` with JSON body

### Expected Outcome / Assert
- HTTP status: 200 OK
- Response body: empty string
- `rideService.finishRide` called exactly once with correct ride ID
- `authUtils` never interacted with (controller does not call it for this endpoint)

### Notes / Additional Info
Most common scenario — driver finishes their last ride and has no next assignment.

---

## Test Case 2

**Title:** Finish ride - successful with next ride returned

### Description
Verify that when a ride is finished and a next ride exists in the queue, the endpoint returns HTTP 200 with the next ride's details.

### Assumptions / Preconditions
- Valid ride ID provided (ID = 1)
- Service returns a RideResponseDTO representing the next ride

### Setup / Arrange
- Create normal FinishRideDTO (interrupted = false)
- Mock `rideService.finishRide` to return next ride DTO (ID = 2, status = ACCEPTED)

### Action / Act
- Send PATCH request to `/api/v1/rides/1/finish` with JSON body

### Expected Outcome / Assert
- HTTP status: 200 OK
- Response body contains:
  - `id`: 2
  - `status`: "ACCEPTED"
- `rideService.finishRide` called exactly once

### Notes / Additional Info
Driver is immediately notified of their next assignment upon finishing the current ride. `status` is compared as a String using `.toString()` on the enum value.

---

## Test Case 3

**Title:** Finish ride - interrupted flag set to true

### Description
Verify that when a ride is finished with `interrupted = true`, the service is called and returns 200 OK.

### Assumptions / Preconditions
- Valid ride ID provided (ID = 1)
- Ride was interrupted mid-journey

### Setup / Arrange
- Create interrupted FinishRideDTO (interrupted = true)
- Mock `rideService.finishRide` to return null

### Action / Act
- Send PATCH request to `/api/v1/rides/1/finish` with interrupted DTO

### Expected Outcome / Assert
- HTTP status: 200 OK
- `rideService.finishRide` called exactly once

### Notes / Additional Info
The controller passes the DTO directly to the service — interrupted flag handling is a service-layer concern. Interrupted rides may have different billing treatment.

---

## Test Case 4

**Title:** Finish ride - ride not found (ResourceNotFoundException)

### Description
Verify that attempting to finish a non-existent ride returns HTTP 404 with an error message.

### Assumptions / Preconditions
- Non-existent ride ID (ID = 99999)

### Setup / Arrange
- Create normal FinishRideDTO (interrupted = false)
- Mock `rideService.finishRide` to throw `ResourceNotFoundException("Ride with id 99999 not found")`

### Action / Act
- Send PATCH request to `/api/v1/rides/99999/finish` with JSON body

### Expected Outcome / Assert
- HTTP status: 404 Not Found
- Response body `$.message` contains: `"Ride with id"`
- `rideService.finishRide` called exactly once

### Notes / Additional Info
Exception is caught by `GlobalExceptionHandler.handleResourceNotFound`. The message check uses `containsString` to avoid coupling to the exact ID value in the error message.

---

## Test Case 5

**Title:** Finish ride - invalid request body (missing interrupted field)

### Description
Verify that sending an empty JSON object `{}` triggers a validation error and returns HTTP 400.

### Assumptions / Preconditions
- Request body is `{}`
- `interrupted` field in `FinishRideDTO` is annotated with `@NotNull`
- Controller method is annotated with `@Valid`

### Setup / Arrange
- Use `{}` as request body (no fields set)

### Action / Act
- Send PATCH request to `/api/v1/rides/1/finish` with body `{}`

### Expected Outcome / Assert
- HTTP status: 400 Bad Request
- `rideService.finishRide` never called

### Notes / Additional Info
Validation is triggered by `@Valid` on `@RequestBody`. The `@NotNull` constraint on `interrupted` (declared as `Boolean` wrapper type) rejects null values.

---

## Test Case 6

**Title:** Finish ride - invalid ID format

### Description
Verify that providing a non-numeric path variable returns HTTP 400 Bad Request.

### Assumptions / Preconditions
- Path variable is `"invalid-id"` (cannot be converted to `Long`)

### Setup / Arrange
- Create valid FinishRideDTO (interrupted = false)
- Use `"invalid-id"` as path variable in URL

### Action / Act
- Send PATCH request to `/api/v1/rides/invalid-id/finish`

### Expected Outcome / Assert
- HTTP status: 400 Bad Request
- `rideService.finishRide` never called

### Notes / Additional Info
Spring throws `MethodArgumentTypeMismatchException` when path variable cannot be converted to the declared type (`Long`). This is caught by `GlobalExceptionHandler.handleTypeMismatch` — note: import must be `org.springframework.web.method.annotation.MethodArgumentTypeMismatchException`, not the messaging variant.

---

## Test Case 7

**Title:** Finish ride - missing content type

### Description
Verify that a request without a `Content-Type` header returns HTTP 415 Unsupported Media Type.

### Assumptions / Preconditions
- No `Content-Type` header set in request

### Setup / Arrange
- Create valid FinishRideDTO (interrupted = false)
- Send request without `.contentType(MediaType.APPLICATION_JSON)`

### Action / Act
- Send PATCH request to `/api/v1/rides/1/finish` without Content-Type header

### Expected Outcome / Assert
- HTTP status: 415 Unsupported Media Type
- `rideService.finishRide` never called

### Notes / Additional Info
Spring throws `HttpMediaTypeNotSupportedException` when Content-Type is absent or unsupported. Caught by `GlobalExceptionHandler.handleUnsupportedMediaType`.

---

## Test Case 8

**Title:** Finish ride - with zero ID

### Description
Verify that using ID = 0 returns HTTP 404 Not Found.

### Assumptions / Preconditions
- Ride ID is 0 (no ride exists with ID 0)

### Setup / Arrange
- Create normal FinishRideDTO (interrupted = false)
- Mock `rideService.finishRide` to throw `ResourceNotFoundException` for ID 0

### Action / Act
- Send PATCH request to `/api/v1/rides/0/finish` with JSON body

### Expected Outcome / Assert
- HTTP status: 404 Not Found
- `rideService.finishRide` called exactly once

### Notes / Additional Info
Edge case — ID 0 is a valid `Long` so it passes type conversion, but no ride exists with this ID at the service level.

---

## Test Case 9

**Title:** Finish ride - with negative ID

### Description
Verify that using a negative ID returns HTTP 404 Not Found.

### Assumptions / Preconditions
- Ride ID is -1 (no ride exists with negative ID)

### Setup / Arrange
- Create normal FinishRideDTO (interrupted = false)
- Mock `rideService.finishRide` to throw `ResourceNotFoundException` for ID -1

### Action / Act
- Send PATCH request to `/api/v1/rides/-1/finish` with JSON body

### Expected Outcome / Assert
- HTTP status: 404 Not Found
- `rideService.finishRide` called exactly once

### Notes / Additional Info
Edge case — negative IDs are valid `Long` values so they pass type conversion, but no ride exists with a negative ID at the service level.

---