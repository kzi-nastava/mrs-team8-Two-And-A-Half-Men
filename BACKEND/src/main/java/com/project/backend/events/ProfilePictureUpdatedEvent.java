package com.project.backend.events;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProfilePictureUpdatedEvent {
    private String picturePath;
}
