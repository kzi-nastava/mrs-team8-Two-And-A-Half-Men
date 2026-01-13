package com.project.backend.repositories;

import com.project.backend.models.UpdateRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UpdateRequestRepository extends JpaRepository<UpdateRequest, Long> {
}
