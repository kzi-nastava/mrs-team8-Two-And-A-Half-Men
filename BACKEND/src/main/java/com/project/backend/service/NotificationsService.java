package com.project.backend.service;

import com.project.backend.events.NotificationCreatedEvent;
import com.project.backend.models.AppUser;
import com.project.backend.models.Notification;
import com.project.backend.repositories.NotificationRepository;
import com.project.backend.util.JsonUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

@Service
public class NotificationsService {
    private static final ObjectMapper MAPPER = new ObjectMapper();
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public NotificationsService(NotificationRepository notificationRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.notificationRepository = notificationRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationToUser(AppUser user, String title, String message, Map<String, Object> additionalData) {
        var notification = Notification.builder()
                .appUser(user)
                .title(title)
                .message(message)
                .data(additionalData != null ? JsonUtil.toJson(additionalData) : null)
                .build();

        notificationRepository.save(notification);
        applicationEventPublisher.publishEvent(new NotificationCreatedEvent(notification));
    }
}
