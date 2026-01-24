package com.project.backend.DTO.Ride;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NoteRequestDTO {

    @NotBlank
    @Size(max = 500)
    private String noteText;
}