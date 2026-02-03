package com.project.backend.DTO.LiveSupport;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendMessageRequestDTO {

    @NotBlank(message = "Content cannot be empty")
    private String content;
}