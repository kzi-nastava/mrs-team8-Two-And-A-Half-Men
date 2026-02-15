package com.project.backend.events;

import com.project.backend.models.AppUser;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProfileChangeRequestHandled {
    private AppUser user;
    private boolean isApproved;
}
