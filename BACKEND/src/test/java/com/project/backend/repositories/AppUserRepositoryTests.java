package com.project.backend.repositories;

import com.project.backend.models.AppUser;
import com.project.backend.repositories.fixtures.AppUserRepositoryTestsFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataJpa tests for AppUserRepository custom query methods.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use real database, not in-memory
@ActiveProfiles("test")
public class AppUserRepositoryTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUserRepositoryTestsFixture fixture;

    @BeforeEach
    void setUp() {
        fixture = new AppUserRepositoryTestsFixture();
    }

    // ==================== Test Case 1: Find users by email list - all emails exist ====================


    @Tag("Student1")
    @Tag("findByEmailIn")
    @Test
    void testFindByEmailIn_WhenAllEmailsExist_ReturnsAllUsers() {
        // Arrange
        AppUser user1 = entityManager.persistAndFlush(fixture.createUser1());
        AppUser user2 = entityManager.persistAndFlush(fixture.createUser2());
        AppUser user3 = entityManager.persistAndFlush(fixture.createUser3());
        List<String> emails = fixture.createAllExistingEmailsList();

        // Act
        List<AppUser> result = appUserRepository.findByEmailIn(emails);

        // Assert
        assertThat(result).hasSize(3);
        assertThat(result).extracting(AppUser::getEmail)
                .containsExactlyInAnyOrder(
                        AppUserRepositoryTestsFixture.USER_EMAIL_1,
                        AppUserRepositoryTestsFixture.USER_EMAIL_2,
                        AppUserRepositoryTestsFixture.USER_EMAIL_3
                );
        assertThat(result).extracting(AppUser::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId(), user3.getId());
    }

    // ==================== Test Case 2: Find users by email list - no emails exist ====================


    @Tag("Student1")
    @Tag("findByEmailIn")
    @Test
    void testFindByEmailIn_WhenNoEmailsExist_ReturnsEmptyList() {
        // Arrange
        // Save a user with a different email to ensure database is not empty
        entityManager.persistAndFlush(fixture.createUser1());
        List<String> nonExistentEmails = fixture.createNonExistentEmailsList();

        // Act
        List<AppUser> result = appUserRepository.findByEmailIn(nonExistentEmails);

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== Test Case 3: Find users by email list - some emails exist ====================


    @Tag("Student1")
    @Tag("findByEmailIn")
    @Test
    void testFindByEmailIn_WhenSomeEmailsExist_ReturnsOnlyMatchingUsers() {
        // Arrange
        AppUser user1 = entityManager.persistAndFlush(fixture.createUser1());
        AppUser user2 = entityManager.persistAndFlush(fixture.createUser2());
        // user3 is not saved, so email3 won't match
        List<String> mixedEmails = fixture.createMixedEmailsList();

        // Act
        List<AppUser> result = appUserRepository.findByEmailIn(mixedEmails);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result).extracting(AppUser::getEmail)
                .containsExactlyInAnyOrder(
                        AppUserRepositoryTestsFixture.USER_EMAIL_1,
                        AppUserRepositoryTestsFixture.USER_EMAIL_2
                );
        assertThat(result).extracting(AppUser::getId)
                .containsExactlyInAnyOrder(user1.getId(), user2.getId());
        // Verify that non-existent emails were ignored
        assertThat(result).extracting(AppUser::getEmail)
                .doesNotContain(AppUserRepositoryTestsFixture.NON_EXISTENT_EMAIL_1)
                .doesNotContain(AppUserRepositoryTestsFixture.NON_EXISTENT_EMAIL_2);
    }

    // ==================== Test Case 4: Find users by email list - empty list ====================


    @Tag("Student1")
    @Tag("findByEmailIn")
    @Test
    void testFindByEmailIn_WhenEmptyList_ReturnsEmptyList() {
        // Arrange
        // Save a user to ensure database is not empty
        entityManager.persistAndFlush(fixture.createUser1());
        List<String> emptyList = fixture.createEmptyEmailsList();

        // Act
        List<AppUser> result = appUserRepository.findByEmailIn(emptyList);

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== Test Case 5: Find users by email list - duplicate emails ====================


    @Tag("Student1")
    @Tag("findByEmailIn")
    @Test
    void testFindByEmailIn_WhenDuplicateEmails_ReturnsUserOnce() {
        // Arrange
        AppUser user1 = entityManager.persistAndFlush(fixture.createUser1());
        List<String> duplicateEmails = fixture.createDuplicateEmailsList();

        // Act
        List<AppUser> result = appUserRepository.findByEmailIn(duplicateEmails);

        // Assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(user1.getId());
        assertThat(result.get(0).getEmail()).isEqualTo(AppUserRepositoryTestsFixture.USER_EMAIL_1);
    }

    // ==================== Test Case 6: Find users by email list - case sensitivity ====================


    @Tag("Student1")
    @Tag("findByEmailIn")
    @Test
    void testFindByEmailIn_CaseSensitivity_DocumentsActualBehavior() {
        // Arrange
        AppUser user = entityManager.persistAndFlush(fixture.createUserWithLowercaseEmail());
        List<String> uppercaseEmails = fixture.createUppercaseEmailsList();

        // Act
        List<AppUser> result = appUserRepository.findByEmailIn(uppercaseEmails);

        // If case-sensitive (typical PostgreSQL default):
        assertThat(result).isEmpty();
    }
}