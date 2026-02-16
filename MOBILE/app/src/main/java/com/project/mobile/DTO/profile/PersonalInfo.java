package com.project.mobile.DTO.profile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalInfo {
    private long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String email;
    private String imgSrc;
    private String role;
    private boolean blocked;
    private String blockReason;

    public boolean isDriver() {
        return "DRIVER".equalsIgnoreCase(role);
    }
}
