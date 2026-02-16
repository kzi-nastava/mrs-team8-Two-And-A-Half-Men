package com.project.mobile.DTO.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordChangeRequest {
    private String oldPassword;
    private String newPassword;
    private String confirmNewPassword;
}
