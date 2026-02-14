# Unit Test Scenarios - Repository Layer Tests

This document outlines the DataJPA test scenarios for **custom repository methods** used in the `RideBookingServiceImpl`. We only test custom query methods that we've created, not Spring's built-in methods (which are already tested by Spring).

## Table of Contents

### AppUserRepository Tests
| Test Case | Title |
|-----------|-------------|
| [Test Case 1](#test-case-1) | Find users by email list - all emails exist |
| [Test Case 2](#test-case-2) | Find users by email list - no emails exist |
| [Test Case 3](#test-case-3) | Find users by email list - some emails exist |
| [Test Case 4](#test-case-4) | Find users by email list - empty list |
| [Test Case 5](#test-case-5) | Find users by email list - duplicate emails |
| [Test Case 6](#test-case-6) | Find users by email list - case sensitivity |

---

## AppUserRepository Tests

The `findByEmailIn()` method is a **derived query method** created by Spring Data JPA from the method name. While Spring generates the implementation, we test it to ensure it behaves correctly with our data model and handles edge cases properly.

### Test Case 1
**Title:** Find users by email list - all emails exist

**Description**  
Verify that `findByEmailIn()` returns all users when all provided emails exist in the database.

**Assumptions / Preconditions**  
- Multiple users with different emails exist in the database.

**Setup / Arrange**  
- Save 3 users with different emails to the database.
- Create a list containing all 3 email addresses.

**Action / Act**  
- Call `findByEmailIn()` with the email list.

**Expected Outcome / Assert**  
- The method returns a list of size 3.
- All returned users have emails matching the input list.
- The returned list contains all expected users (verified by checking IDs).
- The order may vary (derived queries don't guarantee order unless specified).

**Notes / Additional Info**  
This verifies the happy path where all emails have corresponding users.

---

### Test Case 2
**Title:** Find users by email list - no emails exist

**Description**  
Verify that `findByEmailIn()` returns an empty list when none of the provided emails match any users.

**Assumptions / Preconditions**  
- No users with the specified emails exist in the database.
- Other users may exist in the database with different emails.

**Setup / Arrange**  
- Optionally save some users with different emails.
- Create a list of email addresses that don't exist in the database.

**Action / Act**  
- Call `findByEmailIn()` with the non-existent email list.

**Expected Outcome / Assert**  
- The method returns an empty list.
- No exceptions are thrown.

**Notes / Additional Info**  
This is a common scenario in the ride booking flow when passengers are all new users.

---

### Test Case 3
**Title:** Find users by email list - some emails exist

**Description**  
Verify that `findByEmailIn()` returns only the users whose emails match, ignoring non-existent emails.

**Assumptions / Preconditions**  
- Some users exist with emails in the search list, others do not.

**Setup / Arrange**  
- Save 2 users with specific emails.
- Create a list with 2 existing emails and 2 non-existent emails (4 total).

**Action / Act**  
- Call `findByEmailIn()` with the mixed email list.

**Expected Outcome / Assert**  
- The method returns a list of size 2 (only matching users).
- Only users with matching emails are returned.
- Non-existent emails are silently ignored (no errors).
- The returned users' IDs match the saved users.

**Notes / Additional Info**  
This is the most common scenario - some passengers are existing users, some are new.

---

### Test Case 4
**Title:** Find users by email list - empty list

**Description**  
Verify that `findByEmailIn()` handles an empty email list gracefully.

**Assumptions / Preconditions**  
- Database may or may not contain users.

**Setup / Arrange**  
- Optionally save some users to the database.
- Create an empty email list.

**Action / Act**  
- Call `findByEmailIn()` with the empty list.

**Expected Outcome / Assert**  
- The method returns an empty list.
- No exceptions are thrown.
- No database queries should be executed (or a query that returns nothing).

**Notes / Additional Info**  
This edge case occurs when no passenger emails are provided in the ride booking.

---

### Test Case 5
**Title:** Find users by email list - duplicate emails

**Description**  
Verify that `findByEmailIn()` handles duplicate emails in the input list correctly.

**Assumptions / Preconditions**  
- A user with a specific email exists in the database.

**Setup / Arrange**  
- Save 1 user with a specific email.
- Create a list containing the same email 3 times.

**Action / Act**  
- Call `findByEmailIn()` with the list containing duplicate emails.

**Expected Outcome / Assert**  
- The method returns a list of size 1 (no duplicates in results).
- The returned user matches the saved user.
- Duplicate emails in the input are handled correctly.

**Notes / Additional Info**  
This tests whether the IN clause handles duplicates properly (SQL IN removes duplicates).

---

### Test Case 6
**Title:** Find users by email list - case sensitivity

**Description**  
Verify that `findByEmailIn()` performs case-sensitive matching (or case-insensitive if your database is configured that way).

**Assumptions / Preconditions**  
- A user with a lowercase email exists in the database.

**Setup / Arrange**  
- Save a user with email "user@example.com" (lowercase).
- Create a list with "USER@EXAMPLE.COM" (uppercase).

**Action / Act**  
- Call `findByEmailIn()` with the uppercase email.

**Expected Outcome / Assert**  
- If your database/column is case-sensitive: returns empty list.
- If your database/column is case-insensitive (default for PostgreSQL): returns the user.
- This test documents your database's actual behavior.

**Notes / Additional Info**  
This test is important because email matching behavior varies by database. PostgreSQL text fields are case-sensitive by default, but emails are often stored in lowercase to avoid issues. Adjust expectations based on your actual setup.

---

## Why We Don't Test Built-in Methods

Methods like `findById()`, `save()`, `saveAll()`, `findAllById()` are provided by Spring Data JPA and are:
- Already tested by the Spring Framework team
- Generated implementations that don't contain custom logic
- Guaranteed to work correctly by Spring

We only test:
- **Custom query methods** with `@Query` annotations
- **Derived query methods** with complex naming (like `findByEmailIn`)
- Methods that might have edge cases with our specific data model

This keeps our test suite focused and maintainable.
