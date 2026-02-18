package com.project.mobile.DTO.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageUploadResponse {
    private boolean ok;
    private String message;
    private String filePath;
}
