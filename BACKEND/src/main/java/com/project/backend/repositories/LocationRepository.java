package com.project.backend.repositories;

import com.project.backend.models.Location;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findAllByGeoHashIn(List<String> hashes);
}
