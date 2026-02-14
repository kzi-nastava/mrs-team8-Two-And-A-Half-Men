package com.project.backend.repositories;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.Route;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.fixtures.RideRepositoryFinishRideTestsFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * DataJpa tests for RideRepository custom query method: findFirstByDriverAndStatusInOrderByCreatedAtAsc
 * This method is critical for the finishRide workflow as it determines the next ride in queue.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use real database, not in-memory
@ActiveProfiles("test")
public class RideRepositoryFinishRideTests {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RideRepository rideRepository;
    private Driver driver1;
    private Driver driver2;
    private Route route;

    @BeforeEach
    void setUp() {
        // Create and persist test drivers
        driver1 = entityManager.persistAndFlush(RideRepositoryFinishRideTestsFixture.createDriver1());
        driver2 = entityManager.persistAndFlush(RideRepositoryFinishRideTestsFixture.createDriver2());

        // Create and persist a route
        route = entityManager.persistAndFlush(RideRepositoryFinishRideTestsFixture.createRoute());
    }

    // ==================== Test Case 1: Single accepted ride exists ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WhenSingleAcceptedRideExists_ReturnsRide() {
        // Arrange
        Ride acceptedRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );
        entityManager.persistAndFlush(acceptedRide);

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of(RideStatus.ACCEPTED)
        );

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(acceptedRide.getId());
        assertThat(result.get().getDriver()).isEqualTo(driver1);
        assertThat(result.get().getStatus()).isEqualTo(RideStatus.ACCEPTED);
    }

    // ==================== Test Case 2: Multiple accepted rides - earliest returned ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WhenMultipleAcceptedRidesExist_ReturnsEarliestCreated() {
        // Arrange
        Ride laterRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.LATER_TIME
        );
        Ride earliestRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.EARLIER_TIME
        );
        Ride middleRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );

        entityManager.persist(laterRide);
        entityManager.persist(earliestRide);
        entityManager.persist(middleRide);
        entityManager.flush();

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of(RideStatus.ACCEPTED)
        );

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(earliestRide.getId());
        assertThat(result.get().getCreatedAt()).isEqualTo(RideRepositoryFinishRideTestsFixture.EARLIER_TIME);
        // Verify later rides were not returned
        assertThat(result.get().getId()).isNotEqualTo(laterRide.getId());
        assertThat(result.get().getId()).isNotEqualTo(middleRide.getId());
    }

    // ==================== Test Case 3: No ride matches status filter ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WhenNoRideMatchesStatusFilter_ReturnsEmpty() {
        // Arrange
        // Create rides with non-matching statuses
        Ride finishedRide = RideRepositoryFinishRideTestsFixture.createFinishedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );
        Ride interruptedRide = RideRepositoryFinishRideTestsFixture.createInterruptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.LATER_TIME
        );

        entityManager.persist(finishedRide);
        entityManager.persist(interruptedRide);
        entityManager.flush();

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of(RideStatus.ACCEPTED)
        );

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== Test Case 4: Multiple drivers present ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WhenMultipleDriversPresent_ReturnsOnlyForQueriedDriver() {
        // Arrange
        Ride driver1Ride = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.LATER_TIME
        );
        Ride driver2EarlierRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver2,
                route,
                RideRepositoryFinishRideTestsFixture.EARLIER_TIME
        );
        Ride driver2LaterRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver2,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );

        entityManager.persist(driver1Ride);
        entityManager.persist(driver2EarlierRide);
        entityManager.persist(driver2LaterRide);
        entityManager.flush();

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of(RideStatus.ACCEPTED)
        );

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(driver1Ride.getId());
        assertThat(result.get().getDriver()).isEqualTo(driver1);
        // Verify driver2's rides were ignored
        assertThat(result.get().getId()).isNotEqualTo(driver2EarlierRide.getId());
        assertThat(result.get().getId()).isNotEqualTo(driver2LaterRide.getId());
    }

    // ==================== Test Case 5: Status filtering correctness ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WithMultipleStatuses_ReturnsEarliestMatchingStatus() {
        // Arrange
        Ride acceptedRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.LATER_TIME
        );
        Ride pendingRide = RideRepositoryFinishRideTestsFixture.createPendingRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.EARLIER_TIME
        );
        Ride finishedRide = RideRepositoryFinishRideTestsFixture.createFinishedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.EARLIEST_TIME
        );

        entityManager.persist(acceptedRide);
        entityManager.persist(pendingRide);
        entityManager.persist(finishedRide);
        entityManager.flush();

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of(RideStatus.ACCEPTED, RideStatus.PENDING)
        );

        // Assert
        assertThat(result).isPresent();
        // Should return pending ride as it's the earliest among ACCEPTED and PENDING
        assertThat(result.get().getId()).isEqualTo(pendingRide.getId());
        assertThat(result.get().getStatus()).isEqualTo(RideStatus.PENDING);
        assertThat(result.get().getCreatedAt()).isEqualTo(RideRepositoryFinishRideTestsFixture.EARLIER_TIME);
        // Verify FINISHED and ACCEPTED rides were ignored
        assertThat(result.get().getId()).isNotEqualTo(acceptedRide.getId());
        assertThat(result.get().getId()).isNotEqualTo(finishedRide.getId());
    }

    // ==================== Test Case 6: Ordering correctness with mixed timestamps ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WithRandomInsertionOrder_ReturnsEarliestByTimestamp() {
        // Arrange
        // Insert rides in random order (not chronologically)
        Ride ride3 = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME.plusHours(3)
        );
        Ride ride1 = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME.plusHours(1)
        );
        Ride earliestRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );
        Ride ride5 = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME.plusHours(5)
        );

        // Persist in non-chronological order
        entityManager.persist(ride3);
        entityManager.persist(ride1);
        entityManager.persist(ride5);
        entityManager.persist(earliestRide);
        entityManager.flush();

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of(RideStatus.ACCEPTED)
        );

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getId()).isEqualTo(earliestRide.getId());
        assertThat(result.get().getCreatedAt()).isEqualTo(RideRepositoryFinishRideTestsFixture.BASE_TIME);
    }

    // ==================== Test Case 7: Empty status list provided ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WhenEmptyStatusListProvided_ReturnsEmpty() {
        // Arrange
        Ride acceptedRide = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );
        Ride pendingRide = RideRepositoryFinishRideTestsFixture.createPendingRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.LATER_TIME
        );

        entityManager.persist(acceptedRide);
        entityManager.persist(pendingRide);
        entityManager.flush();

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of() // Empty status list
        );

        // Assert
        assertThat(result).isEmpty();
    }

    // ==================== Test Case 8: Multiple rides with identical timestamps ====================

    @Tag("Student1")
    @Tag("findFirstByDriverAndStatusInOrderByCreatedAtAsc")
    @Test
    void testFindFirstRide_WhenMultipleRidesWithIdenticalTimestamps_ReturnsOneRide() {
        // Arrange
        // Create multiple rides with same timestamp
        Ride ride1 = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );
        Ride ride2 = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );
        Ride ride3 = RideRepositoryFinishRideTestsFixture.createAcceptedRide(
                driver1,
                route,
                RideRepositoryFinishRideTestsFixture.BASE_TIME
        );

        entityManager.persist(ride1);
        entityManager.persist(ride2);
        entityManager.persist(ride3);
        entityManager.flush();

        // Act
        Optional<Ride> result = rideRepository.findFirstByDriverAndStatusInOrderByCreatedAtAsc(
                driver1,
                List.of(RideStatus.ACCEPTED)
        );

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getDriver()).isEqualTo(driver1);
        assertThat(result.get().getStatus()).isEqualTo(RideStatus.ACCEPTED);
        assertThat(result.get().getCreatedAt()).isEqualTo(RideRepositoryFinishRideTestsFixture.BASE_TIME);
        // Verify one of the rides was returned (deterministic based on DB)
        assertThat(result.get().getId()).isIn(ride1.getId(), ride2.getId(), ride3.getId());
    }
}