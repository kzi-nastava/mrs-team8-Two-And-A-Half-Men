package com.project.backend.DTO.redis;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RedisLocationsDTO {
    private Long id;
    private Double latitude;
    private Double longitude;
}
