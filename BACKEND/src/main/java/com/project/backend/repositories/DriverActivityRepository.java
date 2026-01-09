package com.project.backend.repositories;

import com.project.backend.models.DriverActivity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DriverActivityRepository extends JpaRepository<DriverActivity, Long> {
}
