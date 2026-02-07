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
public class NewNotificationDTO {
    private String title;
    private String message;
    private Long recipientId;
    private String additionalData; // Optional field for any extra data (e.g., ride details, driver info)

    public NewNotificationDTO(Notification notification) {
        this.title = notification.getTitle();
        this.message = notification.getMessage();
        this.recipientId = notification.getAppUser().getId();
        this.additionalData = notification.getData();
    }
}
