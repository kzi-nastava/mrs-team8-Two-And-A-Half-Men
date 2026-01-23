package com.project.backend.listeners;

import com.project.backend.events.ProfilePictureUpdatedEvent;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.nio.file.Files;
import java.nio.file.Path;

@Component
public class ProfileUpdateListener {

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onProfilePictureUpdated(ProfilePictureUpdatedEvent event) {
        System.out.println("Profile picture updated event received.");
        try {
            if (event.getPicturePath() == null) {
                return;
            }
            Files.delete(Path.of(event.getPicturePath()));
        } catch (Exception e) {
            // Handle exceptions appropriately
        }
    }
}
