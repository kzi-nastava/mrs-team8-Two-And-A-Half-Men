package com.project.backend.repositories;

import com.project.backend.models.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findAllByAppUserId(Long userId);

    List<Notification> findAllByAppUserIdAndReadIsFalse(Long userId);

    List<Notification> findAllByAppUserIdAndReadIsTrue(Long userId);
}
