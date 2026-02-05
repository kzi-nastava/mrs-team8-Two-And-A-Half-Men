package com.project.backend.repositories;

import com.project.backend.models.UpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UpdateRequestRepository extends JpaRepository<UpdateRequest, Long> {
    Optional<UpdateRequest> findByDriverId(Long userId);
    List<UpdateRequest> findByDriverIdIn(List<Long> driverIds);
}
