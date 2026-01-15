package com.project.backend.DTO.Profile;

import com.project.backend.models.AdditionalService;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdditionalServiceDTO {
    private Long id;
    private String name;
    private String description;

    public AdditionalServiceDTO(AdditionalService service) {
        this.id = service.getId();
        this.name = service.getName();
        this.description = service.getDescription();
    }
}
