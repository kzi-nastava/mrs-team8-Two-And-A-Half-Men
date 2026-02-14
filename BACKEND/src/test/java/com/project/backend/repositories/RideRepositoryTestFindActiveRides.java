package com.project.backend.repositories;

import com.project.backend.models.Driver;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.fixtures.RideRepositoryTestFindActiveRidesFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // Use real database, not in-memory
@ActiveProfiles("test")
@Tag("Repository")
@Tag("Student3")
public class RideRepositoryTestFindActiveRides {
    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private RideRepository rideRepository;

    private RideRepositoryTestFindActiveRidesFixtures fixture;

    @BeforeEach
    void setUp() {
        fixture = new RideRepositoryTestFindActiveRidesFixtures();
    }
    @Tag("Student3")
    @Test
    void testFindActiveRides_AllMatches() {
        Driver driver1 = entityManager.persistAndFlush(fixture.createDriver1());
        Driver driver2 = entityManager.persistAndFlush(fixture.createDriver2());

        Ride ride1 = entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.ACCEPTED));
        Ride ride2 = entityManager.persistAndFlush(fixture.createRide(driver2, RideStatus.ACTIVE));
        Ride ride3 = entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.ACTIVE));

        List<RideStatus> statuses = fixture.createActiveStatusesList();
        List<Driver> drivers = fixture.createDriversList(driver1, driver2);
        List<Ride> result = rideRepository.findActiveRides(statuses, drivers);

        assertThat(result).hasSize(3);
        assertThat(result).extracting(Ride::getId)
                .containsExactlyInAnyOrder(ride1.getId(), ride2.getId(), ride3.getId());
        assertThat(result).extracting(Ride::getStatus)
                .allMatch(status -> statuses.contains(status));
        assertThat(result).extracting(Ride::getDriver)
                .allMatch(driver -> drivers.contains(driver));
    }
    @Tag("Student3")
    @Test
    void testFindActiveRides_NoMatches() {
        Driver driver1 = entityManager.persistAndFlush(fixture.createDriver1());
        Driver driver2 = entityManager.persistAndFlush(fixture.createDriver2());


        entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.FINISHED));
        entityManager.persistAndFlush(fixture.createRide(driver2, RideStatus.CANCELLED));

        List<RideStatus> statuses = fixture.createActiveStatusesList();
        List<Driver> drivers = fixture.createDriversList(driver1, driver2);

        List<Ride> result = rideRepository.findActiveRides(statuses, drivers);

        assertThat(result).isEmpty();
    }
    @Tag("Student3")
    @Test
    void testFindActiveRides_StatusMatchDriverNo() {
        Driver driver1 = entityManager.persistAndFlush(fixture.createDriver1());
        Driver driver2 = entityManager.persistAndFlush(fixture.createDriver2());
        Driver driver3 = entityManager.persistAndFlush(fixture.createDriver3());

        entityManager.persistAndFlush(fixture.createRide(driver3, RideStatus.ACCEPTED));
        entityManager.persistAndFlush(fixture.createRide(driver3, RideStatus.ACTIVE));

        List<RideStatus> statuses = fixture.createActiveStatusesList();
        List<Driver> drivers = fixture.createDriversList(driver1, driver2);

        List<Ride> result = rideRepository.findActiveRides(statuses, drivers);

        assertThat(result).isEmpty();
    }
    @Tag("Student3")
    @Test
    void testFindActiveRides_DriverMatchStatusDont() {
        Driver driver1 = entityManager.persistAndFlush(fixture.createDriver1());
        Driver driver2 = entityManager.persistAndFlush(fixture.createDriver2());

        entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.FINISHED));
        entityManager.persistAndFlush(fixture.createRide(driver2, RideStatus.CANCELLED));

        List<RideStatus> statuses = fixture.createActiveStatusesList();
        List<Driver> drivers = fixture.createDriversList(driver1, driver2);

        List<Ride> result = rideRepository.findActiveRides(statuses, drivers);
        assertThat(result).isEmpty();
    }
    @Tag("Student3")
    @Test
    void testFindActiveRides_EmptyStatus() {
        Driver driver1 = entityManager.persistAndFlush(fixture.createDriver1());

        entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.ACCEPTED));

        List<RideStatus> emptyStatuses = fixture.createEmptyStatusesList();
        List<Driver> drivers = fixture.createDriversList(driver1);

        List<Ride> result = rideRepository.findActiveRides(emptyStatuses, drivers);

        assertThat(result).isEmpty();
    }
    @Tag("Student3")
    @Test
    void testFindActiveRides_Order() throws InterruptedException {
        Driver driver1 = entityManager.persistAndFlush(fixture.createDriver1());


        Ride oldestRide = entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.ACCEPTED));

        Ride middleRide = entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.ACTIVE));

        Ride newestRide = entityManager.persistAndFlush(fixture.createRide(driver1, RideStatus.ACCEPTED));

        List<RideStatus> statuses = fixture.createActiveStatusesList();
        List<Driver> drivers = fixture.createDriversList(driver1);

        List<Ride> result = rideRepository.findActiveRides(statuses, drivers);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getId()).isEqualTo(newestRide.getId());
        assertThat(result.get(1).getId()).isEqualTo(middleRide.getId());
        assertThat(result.get(2).getId()).isEqualTo(oldestRide.getId());
    }
}
