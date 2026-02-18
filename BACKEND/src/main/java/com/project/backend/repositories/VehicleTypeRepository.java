package com.project.backend.repositories;

import com.project.backend.DTO.VehicleTypeDTO;
import com.project.backend.models.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleTypeRepository extends JpaRepository<VehicleType, Long> {
    @Query("""
        select new com.project.backend.DTO.VehicleTypeDTO(v.id, v.typeName, v.description, v.price)
        from VehicleType v ORDER BY v.typeName
    """)
    List<VehicleTypeDTO> findAllDTOs();
}
