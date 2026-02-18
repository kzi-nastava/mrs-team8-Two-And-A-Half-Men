package com.project.backend.listeners;

import com.project.backend.events.ProfileChangeRequestHandled;
import com.project.backend.events.ProfilePictureUpdatedEvent;
import com.project.backend.service.NotificationsService;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class ProfileListener {

    private final NotificationsService notificationsService;

    public ProfileListener(NotificationsService notificationsService) {
        this.notificationsService = notificationsService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProfilePictureUpdated(ProfilePictureUpdatedEvent event) {
        try {
            if (event.getPicturePath() == null) {
                return;
            }
            var segments = event.getPicturePath().split("/");
            Path path = Paths.get(String.join(File.separator, segments));
            Files.delete(path);
        } catch (Exception e) {
            // Handle exceptions appropriately
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUpdateRequestHandled(ProfileChangeRequestHandled event) {
        notificationsService.sendNotificationToUser(event.getUser(),
                "Profile Update Request",
                (event.isApproved() ?
                        "Your profile update request has been approved." :
                        "Your profile update request has been rejected."),
                "/profile"
                );
    }
}
