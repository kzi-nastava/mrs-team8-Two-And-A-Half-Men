package com.project.backend.repositories;

import com.project.backend.models.Driver;
import com.project.backend.models.DriverActivity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface DriverActivityRepository extends JpaRepository<DriverActivity, Long> {
    List<DriverActivity> findByDriverAndStartTimeAfter(Driver driver, LocalDateTime startTime);

    List<DriverActivity> findByDriverAndEndTimeIsNull(Driver driver);
}
