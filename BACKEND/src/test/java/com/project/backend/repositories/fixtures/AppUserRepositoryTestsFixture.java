package com.project.backend.repositories.fixtures;

import com.project.backend.models.AppUser;
import com.project.backend.models.Customer;
import lombok.Getter;

import java.util.List;

/**
 * Fixture class for Repository DataJpa tests.
 * Provides factory methods for creating test entities for custom repository method testing.
 */
@Getter
public class AppUserRepositoryTestsFixture {

    // Test email addresses
    public static final String USER_EMAIL_1 = "user1@example.com";
    public static final String USER_EMAIL_2 = "user2@example.com";
    public static final String USER_EMAIL_3 = "user3@example.com";
    public static final String NON_EXISTENT_EMAIL_1 = "nonexistent1@example.com";
    public static final String NON_EXISTENT_EMAIL_2 = "nonexistent2@example.com";
    public static final String UPPERCASE_EMAIL = "USER@EXAMPLE.COM";
    public static final String LOWERCASE_EMAIL = "user@example.com";

    // ==================== AppUser Factory Methods ====================

    /**
     * Creates a user with specified details
     */
    public AppUser createUser(String firstName, String lastName, String email) {
        Customer user = new Customer();
        user.setId(null);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPassword("password123");
        user.setPhoneNumber("+1234567890");
        user.setAddress("123 Test Street");
        user.setIsActive(Boolean.TRUE);
        user.setIsBlocked(Boolean.FALSE);
        user.setBlockReason(null);
        user.setToken(null);
        user.setTokenExpiration(null);
        user.setImgSrc(null);
        return user;
    }

    public AppUser createUser1() {
        return createUser("John", "Doe", USER_EMAIL_1);
    }

    public AppUser createUser2() {
        return createUser("Jane", "Smith", USER_EMAIL_2);
    }

    public AppUser createUser3() {
        return createUser("Bob", "Johnson", USER_EMAIL_3);
    }

    public AppUser createUserWithLowercaseEmail() {
        return createUser("Lower", "Case", LOWERCASE_EMAIL);
    }

    // ==================== Email List Factory Methods ====================

    /**
     * Creates a list of all existing user emails
     */
    public List<String> createAllExistingEmailsList() {
        return List.of(USER_EMAIL_1, USER_EMAIL_2, USER_EMAIL_3);
    }

    /**
     * Creates a list of non-existent emails
     */
    public List<String> createNonExistentEmailsList() {
        return List.of(NON_EXISTENT_EMAIL_1, NON_EXISTENT_EMAIL_2);
    }

    /**
     * Creates a mixed list with some existing and some non-existent emails
     */
    public List<String> createMixedEmailsList() {
        return List.of(USER_EMAIL_1, USER_EMAIL_2, NON_EXISTENT_EMAIL_1, NON_EXISTENT_EMAIL_2);
    }

    /**
     * Creates an empty email list
     */
    public List<String> createEmptyEmailsList() {
        return List.of();
    }

    /**
     * Creates a list with duplicate emails
     */
    public List<String> createDuplicateEmailsList() {
        return List.of(USER_EMAIL_1, USER_EMAIL_1, USER_EMAIL_1);
    }

    /**
     * Creates a list with uppercase version of an email
     */
    public List<String> createUppercaseEmailsList() {
        return List.of(UPPERCASE_EMAIL);
    }
}