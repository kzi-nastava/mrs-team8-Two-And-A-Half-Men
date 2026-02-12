package com.project.backend.service;

import com.project.backend.DTO.Notification.NotificationDTO;
import com.project.backend.events.NotificationCreatedEvent;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.AppUser;
import com.project.backend.models.Notification;
import com.project.backend.repositories.NotificationRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
public class NotificationsService {
    private final NotificationRepository notificationRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public NotificationsService(NotificationRepository notificationRepository, ApplicationEventPublisher applicationEventPublisher) {
        this.notificationRepository = notificationRepository;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void sendNotificationToUser(AppUser user, String title, String message, String additionalData) {
        var notification = Notification.builder()
                .appUser(user)
                .title(title)
                .message(message)
                .data(additionalData)
                .timestamp(LocalDateTime.now())
                .build();

        notificationRepository.save(notification);
        applicationEventPublisher.publishEvent(new NotificationCreatedEvent(notification));
    }

    public List<NotificationDTO> getAll(Long userId) {
        return notificationRepository
                .findAllByAppUserId(userId)
                .stream()
                .map(NotificationDTO::new)
                .toList();
    }

    public Map<String, Object> markRead(Long userId, Long notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getAppUser().getId().equals(userId)) {
            throw new ForbiddenException("This is not your notification");
        }
        notification.setRead(true);
        notificationRepository.save(notification);
        return Map.of("message", "Notification marked as read");
    }

    public Map<String, Object> markAllRead(Long userId) {
            var notifications = notificationRepository.findAllByAppUserIdAndReadIsFalse(userId);
            notifications.forEach(n -> n.setRead(true));
            notificationRepository.saveAll(notifications);
            return Map.of("message", "All notifications marked as read");
    }

    public Map<String, Object> deleteById(Long userId, Long notificationId) {
        var notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
        if (!notification.getAppUser().getId().equals(userId)) {
            throw new ForbiddenException("This is not your notification");
        }
        notificationRepository.delete(notification);
        return Map.of("message", "Notification deleted");
    }

    public Map<String, Object> deleteRead(Long userId) {
        var notifications = notificationRepository.findAllByAppUserIdAndReadIsTrue(userId);
        notificationRepository.deleteAll(notifications);
        return Map.of("message", "All read notifications deleted");
    }
}
