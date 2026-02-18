package com.project.backend.DTO.Auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserTokenDTO {
    private String accessToken;
    private Long expiresIn;
    private Long id;
    private String email = null;
    private String firstName = null;
    private String lastName = null;
    private String imgUrl = null;
    private String role = null;


    public UserTokenDTO(String accessToken, long expiresIn) {
        this.accessToken = accessToken;
        this.expiresIn = expiresIn;
    }
}
