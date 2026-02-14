# Unit Test Scenarios - Ride Booking Service

`RideBookingService` is responsible for parsing and validating ride booking requests, creating new rides and setting up all the necessary information for the ride, finding suitable driver and then saving the ride to the database. It also handles any exceptions that may occur during the process.
It is also responsible for looking for a suitable driver for scheduled rides and assigning the driver to the ride if a suitable driver is found.

## Table of Contents

| Test Case | Title |
|-----------|-------------|
| [Test Case 1](#test-case-1) | Ride owner does not exist |
| [Test Case 2](#test-case-2) | Ride owner's account is blocked |
| [Test Case 3](#test-case-3) | Invalid schedule time |
| [Test Case 4](#test-case-4) | Vehicle type does not exist |
| [Test Case 5](#test-case-5) | Vehicle type is found correctly |
| [Test Case 6](#test-case-6) | Additional services not specified |
| [Test Case 7](#test-case-7) | Some additional services not found |
| [Test Case 8](#test-case-8) | Additional services are found correctly |
| [Test Case 9](#test-case-9) | Route id is set and not found |
| [Test Case 10](#test-case-10) | Route id is set and found correctly |
| [Test Case 11](#test-case-11) | Route id is not set but route is |
| [Test Case 12](#test-case-12) | No passenger list provided |
| [Test Case 13](#test-case-13) | Added passengers, only existing users |
| [Test Case 14](#test-case-14) | Added passengers, only non-existing users |
| [Test Case 15](#test-case-15) | Added passengers, mix of existing and non-existing users |
| [Test Case 16](#test-case-16) | Valid schedule time is set |
| [Test Case 17](#test-case-17) | No suitable driver found |
| [Test Case 18](#test-case-18) | Driver is found and assigned to the ride |
| [Test Case 19](#test-case-19) | Scheduled ride does not exist |
| [Test Case 20](#test-case-20) | Scheduled ride already has a driver assigned |
| [Test Case 21](#test-case-21) | Scheduled ride already not PENDING |
| [Test Case 22](#test-case-22) | Scheduled ride no driver found but not the last chance |
| [Test Case 23](#test-case-23) | Scheduled ride no driver found but no more chances left |
| [Test Case 24](#test-case-24) | Scheduled ride suitable driver found |

---

## Test Case 1
**Title:**
Ride owner does not exist

**Description**
This test verifies that when a ride booking request is made with a non-existent ride owner, the system throws an appropriate exception indicating that the ride owner was not found.

**Assumptions / Preconditions**  
- The ride booking request contains a ride owner ID that does not exist in the system.

**Setup / Arrange**  
- Valid request object
- `appUserRepository` mock is set up to return `null` when queried with the non-existent ride owner ID.

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- An exception is thrown indicating that the ride owner was not found.
- The exception message should contain the ride owner ID.
- The `appUserRepository` should be called once with the correct ride owner ID.
- The `rideRepository.save()` should not be called since no ride is created.

**Notes / Additional Info**  
None


---

## Test Case 2

**Title:**
Ride owner's account is blocked

**Description**  
This test verifies that when a ride booking request is made with a blocked ride owner, the system throws an appropriate exception indicating that the ride owner's account is blocked.

**Assumptions / Preconditions**  
- The ride booking request contains a ride owner ID that exists in the system but has a blocked account.

**Setup / Arrange**  
- Valid request object
- `appUserRepository` mock is set up to return an `AppUser` object with `isBlocked` set to `true` when queried with the blocked ride owner ID.
- <Mocks and objects setup>

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- An exception is thrown indicating that the ride owner's account is blocked.
- The exception message should contain the reason for the block.
- The `appUserRepository` should be called once with the correct ride owner ID.
- The `rideRepository.save()` should not be called since no ride is created.

**Notes / Additional Info**  
None

---

## Test Case 3

**Title:**
Invalid schedule time

**Description**  
This test verifies that when a ride booking request is made with an invalid schedule time (e.g., in the past), the system throws an appropriate exception.

**Assumptions / Preconditions**  
- For one execution, the schedule time is set to a past date/time.
- For another execution, the schedule time is set too far in the future (more then MAX_HOURS).

**Setup / Arrange**  
- Valid request object with an invalid schedule time
- Mocks and objects setup

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- An exception is thrown indicating that the schedule time is invalid.
- The `rideRepository.save()` should not be called since no ride is created.

**Notes / Additional Info**  
None

---

## Test Case 4

**Title:**
Vehicle type does not exist

**Description**  
This test verifies that when a ride booking request is made with a non-existent vehicle type, the system throws an appropriate exception.

**Assumptions / Preconditions**  
- The ride booking request contains a vehicle type ID that does not exist in the system.

**Setup / Arrange**  
- Valid request object with a non-existent vehicle type ID
- `vehicleTypeRepository` mock is set up to return `null` when queried with the non-existent vehicle type ID.

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- An exception is thrown indicating that the vehicle type was not found.
- The exception message should contain the vehicle type ID.
- The `vehicleTypeRepository` should be called once with the correct vehicle type ID.
- The `rideRepository.save()` should not be called since no ride is created.

**Notes / Additional Info**  
None


---

## Test Case 5

**Title:**
Vehicle type is found correctly

**Description**  
This test verifies that when a ride booking request is made with a valid vehicle type ID, the system correctly finds and uses the vehicle type.

**Assumptions / Preconditions**  
- The ride booking request contains a vehicle type ID that exists in the system.

**Setup / Arrange**  
- Valid request object with an existing vehicle type ID
- `vehicleTypeRepository` mock is set up to return a valid `VehicleType` object when queried with the existing vehicle type ID.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `vehicleTypeRepository` should be called once with the correct vehicle type ID.
- The `rideRepository.save()` should be called with a properly configured `Ride` object.
- The ride's `vehicleType` property should be set to the vehicle type returned by the `vehicleTypeRepository`.
- The ride's price should be set to the price of the vehicle type.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.
- RideCreatedEvent should be published with the correct ride details.
- RideCreatedEvent should be published after the ride is saved to the repository.

**Notes / Additional Info**  
None

---



## Test Case 6

**Title:**
Additional services not specified

**Description**  
This test verifies that when a ride booking request is made without specifying any additional services, the system correctly processes the request without any additional services.

**Assumptions / Preconditions**  
- The ride booking request does not specify any additional services.

**Setup / Arrange**  
- Valid request object without additional services specified
- For one execution, the additional services field is set to `null`.
- For another execution, the additional services field is set to an empty list.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideRepository.save()` should be called with a `Ride` object that has an empty set of additional services.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.

**Notes / Additional Info**  
None
- RideCreatedEvent should be published after the ride is saved to the repository.


---

## Test Case 7

**Title:**
Some additional services not found

**Description**  
The request is made with a list of additional service IDs, where some of the IDs do not correspond to existing additional services in the system. The test verifies that the system throws an appropriate exception indicating which additional services were not found.

**Assumptions / Preconditions**  
- The ride booking request contains a list of additional service IDs, where at least one ID does not exist in the system.

**Setup / Arrange**  
- Valid request object with a list of additional service IDs, including some non-existent IDs
- `additionalServiceRepository` mock is set up to return a list of `AdditionalService` objects which only include some of the requested additional service IDs

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- An exception is thrown indicating which additional service IDs were not found.
- The `additionalServiceRepository` should be called once with the correct list of additional service IDs
- The `rideRepository.save()` should not be called since no ride is created.

**Notes / Additional Info**  
None


---

## Test Case 8

**Title:**
Additional services are found correctly

**Description**  
This test verifies that when a ride booking request is made with valid additional service IDs, the system correctly finds and uses the additional services.

**Assumptions / Preconditions**  
- The ride booking request contains a list of additional service IDs that all exist in the system.

**Setup / Arrange**  
- Valid request object with a list of existing additional service IDs
- `additionalServiceRepository` mock is set up to return a list of valid `AdditionalService` objects when queried with the existing additional service IDs.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `additionalServiceRepository` should be called once with the correct list of additional service IDs.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes the additional services.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.

**Notes / Additional Info**  
None
- RideCreatedEvent should be published after the ride is saved to the repository.


---

## Test Case 9

**Title:**
Route id is set and not found

**Description**  
This test verifies that when a ride booking request is made with a route ID that does not exist in the system, an appropriate exception is thrown.

**Assumptions / Preconditions**  
- The ride booking request contains a route ID that does not exist in the system.

**Setup / Arrange**  
- Valid request object with a non-existent route ID
- `routeRepository` mock is set up to return `null` when queried with the non-existent route ID

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- An exception is thrown indicating that the route was not found.
- The `routeRepository` should be called once with the correct route ID
- The `rideRepository.save()` should not be called since no ride is created.

**Notes / Additional Info**  
None


---

## Test Case 10

**Title:**
Route id is set and found correctly

**Description**  
This test verifies that when a ride booking request is made with a valid route ID, the system correctly finds and uses the route.

**Assumptions / Preconditions**  
- The ride booking request contains a route ID that exists in the system.

**Setup / Arrange**  
- Valid request object with an existing route ID
- `routeRepository` mock is set up to return a valid `Route` object when queried with the existing route ID.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `routeRepository` should be called once with the correct route ID.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes the route.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.

**Notes / Additional Info**  
None
- RideCreatedEvent should be published after the ride is saved to the repository.


---

## Test Case 11

**Title:**
Route id is not set but route is

**Description**  
This test verifies that when a ride booking request is made without setting a route ID but the route field is set, the system correctly uses the provided route object to create a new route.

**Assumptions / Preconditions**  
- The ride booking request does not contain a route ID but includes a route object with valid data.
- The route object contains valid data for creating a new route.

**Setup / Arrange**  
- Valid request object without a route ID but with a valid route object
- `routeService` mock is set up to return a valid `Route` object when the `createRoute` method is called with the route data from the request.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `routeService` should be called once with the correct route data.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes the newly created route.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.


**Notes / Additional Info**  
- RideCreatedEvent should be published after the ride is saved to the repository.None



---

## Test Case 12

**Title:**
No passenger list provided

**Description**  
This test verifies that when a ride booking request is made without providing a passenger list, the system correctly processes the request without any passengers and only adds the ride owner as a passenger.

**Assumptions / Preconditions**  
- The ride booking request does not include a passenger list.

**Setup / Arrange**  
- Valid request object without a passenger list
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that has only one passenger
- The passenger should have `email`property set to `null`
- The passenger's `user` property should be set to the ride owner.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.

- RideCreatedEvent should be published after the ride is saved to the repository.**Notes / Additional Info**  

None


---

## Test Case 13

**Title:**
Added passengers, only existing users

**Description**  
This test verifies that when a ride booking request is made with a passenger list that includes only existing users, the system correctly processes the request and adds the passengers to the ride.

**Assumptions / Preconditions**  
- The ride booking request includes a passenger list with email addresses that all correspond to existing users in the system.

**Setup / Arrange**  
- Valid request object with a passenger list containing existing user email addresses
- `appUserRepository` mock is set up to return valid `AppUser` objects when queried with the email addresses from the passenger list.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `appUserRepository` should be called once for each email address in the passenger list with the correct email.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes the passenger
- Each passenger should have their `email` property set to `null`
- Each passenger's `user` property should be set to the corresponding `AppUser` object
- The ride owner should also be included as a passenger with their `email` property set to `null` and their `user` property set to the ride owner.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.
- RideCreatedEvent should be published after the ride is saved to the repository.

**Notes / Additional Info**  
None


---

## Test Case 14

**Title:**
Added passengers, only non-existing users

**Description**  
This test verifies that when a ride booking request is made with a passenger list that includes only non-existing users, the system correctly processes the request and adds the passengers to the ride with their email addresses but without user accounts.

**Assumptions / Preconditions**  
- The ride booking request includes a passenger list with email addresses that do not correspond to any existing users in the system.

**Setup / Arrange**  
- Valid request object with a passenger list containing non-existing user email addresses
- `appUserRepository` mock is set up to return empty list when queried with the email addresses from the passenger list.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `appUserRepository` should be called once for each email address in the passenger list with the correct email.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes the passengers
- Each passenger aside from the ride owner should have their `email` property set to the email address from the passenger list and their `user` property set to `null`
- The ride owner should also be included as a passenger with their `email` property set to `null` and their `user` property set to the ride owner.
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.
- RideCreatedEvent should be published after the ride is saved to the repository.

**Notes / Additional Info**  
None


---

## Test Case 15

**Title:**
Added passengers, mix of existing and non-existing users

**Description**  
This test verifies that when a ride booking request is made with a passenger list that includes both existing and non-existing users, the system correctly processes the request and adds the passengers to the ride.

**Assumptions / Preconditions**  
- The ride booking request includes a passenger list with email addresses that include both existing and non-existing users in the system.

**Setup / Arrange**  
- Valid request object with a passenger list containing a mix of existing and non-existing user email addresses
- `appUserRepository` mock is set up to return a list of `AppUser` objects which correspond to the existing user email addresses from the passenger list, and does not include any `AppUser` objects for the non-existing user email addresses.
- `dateTimeService` mock is set up to return a fixed current time

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `appUserRepository` should be called once for each email address in the passenger list with the correct email.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes all passengers
- Each existing user passenger should have their `email` property set to `null` and their `user` property set to the corresponding `AppUser` object
- Each non-existing user passenger should have their `email` property set to the email address from the passenger list and their `user` property set to `null`
- The ride owner should also be included as a passenger with their `email` property set to `null` and their `user` property set to the ride owner.
- RideCreatedEvent should be published after the ride is saved to the repository.- The ride's `createdAt` property should be set to th
e current time returned by the `dateTimeService` mock.

**Notes / Additional Info**  
None



---

## Test Case 16

**Title:**
Valid schedule time is set

**Description**  
This test verifies that when a ride booking request is made with a valid schedule time, the system correctly processes the request and sets the schedule time for the ride.

**Assumptions / Preconditions**  
- The ride booking request contains a schedule time that is valid (e.g., in the future and within the allowed scheduling window).

**Setup / Arrange**  
- Valid request object with a valid schedule time
- `dateTimeService` mock is set up to return a fixed current time that is before the schedule time.
- `dateTimeService` mock is also set up to return a fixed current time that is within the allowed scheduling window when the schedule time is checked against it.

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The ride's `scheduleTime` property should be set to the schedule time from the request object.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes the schedule time.
- Ride's `status` property should be set to `PENDING`
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.

**Notes / Additional Info**  
- RideCreatedEvent should be published after the ride is saved to the repository.None



---

## Test Case 17

**Title:**
No suitable driver found

**Description**  
This test verifies that when a ride booking request is made without a schedule time and no suitable driver is found for the ride the request is denied and an appropriate exception is thrown.

**Assumptions / Preconditions**  
- The ride booking request does not contain a schedule time.


**Setup / Arrange**  
- Valid request object without a schedule time
- `dateTimeService` mock is set up to return a fixed current time.
- The `driverMatchingService` mock is set up to return an empty object for the `findDriverFor` method, indicating that no suitable driver was found for the ride.

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- An exception is thrown indicating that no suitable driver was found for the ride. 
- The `driverMatchingService` should be called once with the correct ride details.
- The `rideRepository.save()` should not be called since no ride is created.

**Notes / Additional Info**  
None


---

## Test Case 18

**Title:**
Driver is found and assigned to the ride

**Description**  
This test verifies that when a ride booking request is made without a schedule time and a suitable driver is found for the ride, the system correctly assigns the driver to the ride and processes the request successfully.

**Assumptions / Preconditions**  
- The ride booking request does not contain a schedule time.
- A suitable driver is found for the ride.

**Setup / Arrange**  
- Valid request object without a schedule time
- `dateTimeService` mock is set up to return a fixed current time.
- The `driverMatchingService` mock is set up to return a valid `Driver` object for the `findDriverFor` method, indicating that a suitable driver was found for the ride.
- `driverMatchingService` mock is set up to return a valid `FindDriverDTO` object when queried with the `findDriverFor` method, which includes the driver details and the distance to the driver.

**Action / Act**  
- Call the `bookRide` method with the request object.

**Expected Outcome / Assert**  
- No exception is thrown.
- The ride's `driver` property should be set to the driver returned by the `driverMatchingService`.
- The ride's `distanceToDriver` property should be set to the distance returned by the `driverMatchingService`.
- The `rideRepository.save()` should be called with a properly configured `Ride` object that includes the assigned driver and distance.
- Ride's `status` property should be set to `ACCEPTED`
- The ride's `createdAt` property should be set to the current time returned by the `dateTimeService` mock.

**Notes / Additional Info**  
- RideCreatedEvent should be published after the ride is saved to the repository.None
- DriverAssignedEvent should be published after the ride is saved to the repository with the correct ride and driver details.



---

## Test Case 19

**Title:**
Scheduled ride does not exist

**Description**  
This test verifies that when trying to find a suitable driver for a scheduled ride, whose `rideId` provided does not correspond to any existing ride in the system, nothing happens (no exception is thrown because this method is called by a scheduler and we don't want it to crash the scheduler).

**Assumptions / Preconditions**  
- The `rideId` provided does not correspond to any existing ride in the system.

**Setup / Arrange**  
- Valid `rideId` that does not exist in the system
- Valid `minutesBefore` value
- `rideBookingRepository` mock is set up to return `null` when queried with the non-existent `rideId`.

**Action / Act**  
- Call the `findDriverForScheduledRide` method with the invalid `rideId` and any `minutesBefore` value.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideBookingRepository` should be called once with the correct `rideId`.
- The `driverMatchingService.findDriverFor` method should not be called since the ride does not exist.
- The `rideRepository.save()` should not be called since no ride is updated.
- No events should be published since no driver is assigned.

**Notes / Additional Info**  
None



---

## Test Case 20

**Title:**
Scheduled ride already has a driver assigned

**Description**  
This test verifies that when trying to find a suitable driver for a scheduled ride that already has a driver assigned, nothing happens (no exception is thrown because this method is called by a scheduler and we don't want it to crash the scheduler).

**Assumptions / Preconditions**  
- The `rideId` provided corresponds to an existing ride in the system that already has a driver assigned.

**Setup / Arrange**  
- Valid `rideId` that corresponds to an existing ride in the system that already has a driver assigned
- Valid `minutesBefore` value
- `rideBookingRepository` mock is set up to return a `Ride` object with a non-null `driver` property when queried with the existing `rideId`.

**Action / Act**  
- Call the `findDriverForScheduledRide` method with the existing `rideId` and any `minutesBefore` value.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideBookingRepository` should be called once with the correct `rideId`.
- The `driverMatchingService.findDriverFor` method should not be called since the ride already has a driver assigned.
- The `rideRepository.save()` should not be called since no ride is updated.
- No events should be published since no driver is assigned.

**Notes / Additional Info**  
None



---

## Test Case 21

**Title:**
Scheduled ride already not PENDING

**Description**  
This test verifies that when trying to find a suitable driver for a scheduled ride that is not in PENDING status, nothing happens (no exception is thrown because this method is called by a scheduler and we don't want it to crash the scheduler).

**Assumptions / Preconditions**  
- The `rideId` provided corresponds to an existing ride in the system that is not in PENDING status.

**Setup / Arrange**  
- Valid `rideId` that corresponds to an existing ride in the system that is not in PENDING status
- Valid `minutesBefore` value
- `rideBookingRepository` mock is set up to return a `Ride` object with a non-null `driver` property when queried with the existing `rideId`.

**Action / Act**  
- Call the `findDriverForScheduledRide` method with the existing `rideId` and any `minutesBefore` value.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideBookingRepository` should be called once with the correct `rideId`.
- The `driverMatchingService.findDriverFor` method should not be called since the ride is not in PENDING status.
- The `rideRepository.save()` should not be called since no ride is updated.
- No events should be published since no driver is assigned.

**Notes / Additional Info**  
None


---

## Test Case 22

**Title:**
Scheduled ride no driver found but not the last chance

**Description**  
This test verifies that when trying to find a suitable driver for a scheduled ride and no suitable driver is found, but the current time is not yet within the last chance window for finding a driver, nothing happens (no exception is thrown because this method is called by a scheduler and we don't want it to crash the scheduler).

**Assumptions / Preconditions**  
- The `rideId` provided corresponds to an existing ride in the system that is in PENDING status.
- The current time is not yet within the last chance window for finding a driver for the ride (i.e., there is still time before the scheduled ride time minus the `minutesBefore` value)

**Setup / Arrange**  
- Valid `rideId` that corresponds to an existing ride in the system that is in PENDING status
- `minutesBefore` value is 0
- `rideBookingRepository` mock is set up to return a `Ride` object with a null `driver` property and a `scheduleTime` that is in the future when queried with the existing `rideId`.

**Action / Act**  
- Call the `findDriverForScheduledRide` method with the existing `rideId` and 0 `minutesBefore` value.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideBookingRepository` should be called once with the correct `rideId`.
- The `driverMatchingService.findDriverFor` method should  be called since the ride is in PENDING status.
- The `rideRepository.save()` should not be called since no ride is updated.
- No events should be published since no driver is assigned.

**Notes / Additional Info**  
None

---

## Test Case 23

**Title:**
Scheduled ride no driver found but no more chances left
**Description**  
This test verifies that when trying to find a suitable driver for a scheduled ride and no suitable driver is found, and the current time is within the last chance window for finding a driver, the system correctly handles this scenario (e.g., by marking the ride as failed or notifying the ride owner).

**Assumptions / Preconditions**  
- The `rideId` provided corresponds to an existing ride in the system that is in PENDING status.
- The current time is within the last chance window for finding a driver for the ride (i.e., there is no time left before the scheduled ride time minus the `minutesBefore` value)

**Setup / Arrange**  
- Valid `rideId` that corresponds to an existing ride in the system that is in PENDING status
- `minutesBefore` value is 0
- `rideBookingRepository` mock is set up to return a `Ride` object with a null `driver` property and a `scheduleTime` that is in the future when queried with the existing `rideId`.

**Action / Act**  
- Call the `findDriverForScheduledRide` method with the existing `rideId` and 0 `minutesBefore` value.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideBookingRepository` should be called once with the correct `rideId`.
- The `driverMatchingService.findDriverFor` method should  be called since the ride is in PENDING status.
- The `rideRepository.save()` should be called since the ride is updated to CANCELLED status.
- RideStatusChangedEvent should be published with the correct ride details and new status.
- DriverAssignedEvent should not be published since no driver is assigned.

**Notes / Additional Info**  
None


---

## Test Case 24

**Title:**
Scheduled ride suitable driver found
**Description**  
This test verifies that when trying to find a suitable driver for a scheduled ride and a suitable driver is found, the system correctly assigns the driver and updates the ride status.

**Assumptions / Preconditions**  
- The `rideId` provided corresponds to an existing ride in the system that is in PENDING status.
- A suitable driver exists for the ride.

**Setup / Arrange**  
- Valid `rideId` that corresponds to an existing ride in the system that is in PENDING status
- `minutesBefore` value is any
- 

**Action / Act**  
- Call the `findDriverForScheduledRide` method with the existing `rideId` and 0 `minutesBefore` value.

**Expected Outcome / Assert**  
- No exception is thrown.
- The `rideBookingRepository` should be called once with the correct `rideId`.
- The `driverMatchingService.findDriverFor` method should  be called since the ride is in PENDING status.
- The `rideRepository.save()` should be called since the ride is updated to ACCEPTED status and assigned a driver.
- RideStatusChangedEvent should be published with the correct ride details and new status.
- DriverAssignedEvent should be published with the correct ride details and assigned driver.

**Notes / Additional Info**  
None

