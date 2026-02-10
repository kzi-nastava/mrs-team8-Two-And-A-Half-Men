package com.project.backend.listeners;

import com.project.backend.DTO.Notification.NotificationDTO;
import com.project.backend.events.NotificationCreatedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class NotificationsListener {
    private final SimpMessagingTemplate simpMessagingTemplate;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onCreated(NotificationCreatedEvent event) {
        simpMessagingTemplate.convertAndSend(
                "/topic/notifications/" + event.getNotification().getAppUser().getId(),
                new NotificationDTO(event.getNotification())
        );
    }
}
