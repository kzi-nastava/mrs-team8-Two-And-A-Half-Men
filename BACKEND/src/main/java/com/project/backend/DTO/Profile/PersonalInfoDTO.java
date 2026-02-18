package com.project.backend.DTO.Profile;

import com.project.backend.models.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PersonalInfoDTO {
    private Long id;
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String address;
    private String email;
    private String imgSrc;
    private String role;
    private boolean isBlocked;
    private String blockReason;

    public PersonalInfoDTO(AppUser user) {
        this.id = user.getId();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phoneNumber = user.getPhoneNumber();
        this.address = user.getAddress();
        this.email = user.getEmail();
        this.imgSrc = user.getImgSrc();
        this.role = user.getRole().name();
        this.isBlocked = user.getIsBlocked();
        this.blockReason = user.getBlockReason();
    }
}
