package com.project.backend.listeners;

import com.project.backend.events.RideCreatedEvent;
import com.project.backend.events.RideFinishedEvent;
import com.project.backend.service.EmailBodyGeneratorService;
import com.project.backend.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class RideListener {
    private final EmailService emailService;
    private final EmailBodyGeneratorService emailBodyGeneratorService;
    @Value("${frontend.url}")
    private String frontendUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void linkedEmail(RideCreatedEvent event) {
        var ride = event.getRide();
        var ownerName = ride.getRideOwner().getFirstName() + " " + ride.getRideOwner().getLastName();
        var url = frontendUrl + "/ride/" + ride.getId() + "/track?accessToken=";
        for (var passenger : ride.getPassengers()) {
            try {
                emailService.sendEmail(
                        passenger.getEmail() != null ?
                                passenger.getEmail() :
                                passenger.getUser().getEmail(),
                        "New ride",
                        emailBodyGeneratorService.generatePassengerAddedEmail(
                                ownerName,
                                url + passenger.getAccessToken()
                        )
                );
            }
            catch (Exception e) {
                //throw new RuntimeException(e);
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void ratingEmail(RideFinishedEvent event) {
        var ride = event.getRide();

        var ownerName = ride.getRideOwner().getFirstName() + " " + ride.getRideOwner().getLastName();
        var driverName = ride.getDriver().firstNameAndLastName();
        var rideStartDate = ride.getStartTime().toLocalDate().toString();
        var url = frontendUrl + "/rides/" + ride.getId() + "/rating?accessToken=";
        for (var passenger : ride.getPassengers()) {
            try {
                emailService.sendEmail(
                        passenger.getEmail() != null ?
                                passenger.getEmail() :
                                passenger.getUser().getEmail(),
                        "Rate finished ride",
                        emailBodyGeneratorService.generateRideRatingEmailBody(
                                ownerName,
                                driverName,
                                rideStartDate,
                                url + passenger.getAccessToken()
                        )
                );
            }
            catch (Exception e) {
                //throw new RuntimeException(e);
            }
        }
    }
}
