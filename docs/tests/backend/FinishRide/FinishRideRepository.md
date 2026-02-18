# Unit Test Scenarios - RideRepository Custom Query Tests

This document defines DataJPA test scenarios for the custom repository method used in the ride finishing workflow.

The method tested here determines which ride becomes the driver's next active ride after one finishes.

---

## Tested Repository Method

java
findFirstByDriverAndStatusInOrderByCreatedAtAsc(
    Driver driver,
    List<RideStatus> statuses
)

# Table of Contents

| Test Case | Title |
|-----------|-------|
| Test Case 1 | Single accepted ride exists |
| Test Case 2 | Multiple accepted rides - earliest returned |
| Test Case 3 | No ride matches status filter |
| Test Case 4 | Multiple drivers present |
| Test Case 5 | Status filtering correctness |
| Test Case 6 | Ordering correctness with mixed timestamps |
| Test Case 7 | Empty status list provided |
| Test Case 8 | Multiple rides with identical timestamps |

---

## Test Case 1

**Title:** Single accepted ride exists

### Description
Verify that the method correctly returns a ride when only one matching ride exists.

### Assumptions / Preconditions
- One driver exists.
- One ride exists with status ACCEPTED.

### Setup / Arrange
- Persist driver.
- Persist a ride with status ACCEPTED for that driver.

### Action / Act
- Query repository using ACCEPTED status filter.

### Expected Outcome / Assert
- Optional result is present.
- Returned ride belongs to the correct driver.
- Ride status matches expected status.

### Notes / Additional Info
Represents the simplest real-world case.

---

## Test Case 2

**Title:** Multiple accepted rides - earliest returned

### Description
Verify that when multiple matching rides exist, the earliest created ride is returned.

### Assumptions / Preconditions
- Multiple rides exist for same driver.
- All rides have status ACCEPTED.

### Setup / Arrange
- Persist driver.
- Persist rides with different createdAt timestamps.
- Ensure timestamps increase chronologically.

### Action / Act
- Call repository query.

### Expected Outcome / Assert
- Only one ride is returned.
- Returned ride has the smallest createdAt value.
- Later rides are ignored.

### Notes / Additional Info
This ensures correct queue ordering.

---

## Test Case 3

**Title:** No ride matches status filter

### Description
Verify that no ride is returned if driver rides exist but none match the requested statuses.

### Assumptions / Preconditions
- Driver has rides with statuses FINISHED or INTERRUPTED.

### Setup / Arrange
- Persist rides for driver with non-matching statuses.

### Action / Act
- Query repository using ACCEPTED filter.

### Expected Outcome / Assert
- Result Optional is empty.

### Notes / Additional Info
Service layer later interprets this as no next ride available.

---

## Test Case 4

**Title:** Multiple drivers present

### Description
Verify that only rides belonging to the provided driver are considered.

### Assumptions / Preconditions
- Two drivers exist.
- Both have ACCEPTED rides.

### Setup / Arrange
- Persist two drivers.
- Add accepted rides for both drivers.

### Action / Act
- Query using first driver.

### Expected Outcome / Assert
- Result belongs only to queried driver.
- Rides of other drivers are ignored.

### Notes / Additional Info
Prevents cross-driver ride assignment.

---

## Test Case 5

**Title:** Status filtering correctness

### Description
Verify filtering behavior when multiple statuses are provided.

### Assumptions / Preconditions
- Driver has rides in statuses ACCEPTED, PENDING, FINISHED.

### Setup / Arrange
- Persist rides with mixed statuses.

### Action / Act
- Query using statuses [ACCEPTED, PENDING].

### Expected Outcome / Assert
- Only rides with provided statuses are considered.
- Earliest among matching rides is returned.

### Notes / Additional Info
Confirms correct StatusIn clause handling.

---

## Test Case 6

**Title:** Ordering correctness with mixed timestamps

### Description
Verify ordering when rides are inserted in random order.

### Assumptions / Preconditions
- Creation order differs from insertion order.

### Setup / Arrange
- Persist rides with manually assigned timestamps.
- Insert rides in random order.

### Action / Act
- Query repository.

### Expected Outcome / Assert
- Ride with earliest timestamp is returned regardless of insertion order.

### Notes / Additional Info
Ensures database ordering logic works correctly.

---

## Test Case 7

**Title:** Empty status list provided

### Description
Verify behavior when an empty status list is provided.

### Assumptions / Preconditions
- Rides exist for driver.

### Setup / Arrange
- Persist rides normally.

### Action / Act
- Query repository with empty status list.

### Expected Outcome / Assert
- Result is empty.
- No rides are returned.

### Notes / Additional Info
Confirms safe handling of edge-case inputs.

---

## Test Case 8

**Title:** Multiple rides with identical timestamps

### Description
Verify repository behavior when rides share identical creation timestamps.

### Assumptions / Preconditions
- Multiple rides exist with same timestamp.

### Setup / Arrange
- Persist rides with identical createdAt.

### Action / Act
- Query repository.

### Expected Outcome / Assert
- One ride is returned.
- Returned ride belongs to correct driver.
- Result ordering is deterministic based on DB behavior.

### Notes / Additional Info
Prevents unstable behavior in rare timestamp collisions.

---