package com.project.backend.events;

import com.project.backend.models.Notification;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class NotificationCreatedEvent {
    private final Notification notification;
}
