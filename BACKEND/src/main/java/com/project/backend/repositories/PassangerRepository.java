package com.project.backend.repositories;

import com.project.backend.models.Passanger;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PassangerRepository extends JpaRepository<Passanger, Long> {
}
