package com.project.backend.DTO.Notification;

import com.project.backend.models.Notification;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;
    private String title;
    private String message;
    private Long recipientId;
    private boolean read;
    private String additionalData; // Optional field for any extra data (e.g., ride details, driver info)

    public NotificationDTO(Notification notification) {
        this.id = notification.getId();
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.recipientId = notification.getAppUser().getId();
        this.additionalData = notification.getData();
        this.read = notification.isRead();
    }
}
