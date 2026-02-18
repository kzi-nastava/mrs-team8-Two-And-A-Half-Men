package com.project.backend.repositories;

import com.project.backend.models.Driver;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findAllByIdIn(List<Long> ids);
}
