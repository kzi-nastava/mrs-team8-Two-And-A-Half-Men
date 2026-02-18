package com.project.backend.listeners;

import com.project.backend.events.DriverAssignedEvent;
import com.project.backend.events.RideCreatedEvent;
import com.project.backend.events.RideFinishedEvent;
import com.project.backend.events.RideStatusChangedEvent;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class RideListener {
    private final EmailService emailService;
    private final RideRepository rideRepository;
    private final EmailBodyGeneratorService emailBodyGeneratorService;
    private final SchedulingService schedulingService;
    private final RideNotificationService rideNotificationService;
    private final NotificationsService notificationsService;
    private final RideBookingService rideBookingService;
    @Value("${frontend.url}")
    private String frontendUrl;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void notifyDriver(DriverAssignedEvent event) {
        System.out.println("Driver assigned event received for ride " + event.getRide().getId() + " and driver " + event.getRide().getDriver().getId());
        notificationsService.sendNotificationToUser(event.getRide().getDriver(),
                "New ride",
                "You have been assigned a new ride",
                "/rides/"+event.getRide().getId()
        );
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void ratingEmail(RideFinishedEvent event) {
        var ride = event.getRide();

        var ownerName = ride.getRideOwner().getFirstName() + " " + ride.getRideOwner().getLastName();
        var driverName = ride.getDriver().firstNameAndLastName();
        var rideStartDate = ride.getStartTime().toLocalDate().toString();
        var url = frontendUrl + "/rides/" + ride.getId() + "?view=rate&accessToken=";
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

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void linkedEmail(RideCreatedEvent event) {
        var ride = event.getRide();
        var ownerName = ride.getRideOwner().getFirstName() + " " + ride.getRideOwner().getLastName();
        var url = frontendUrl + "/rides/" + ride.getId() + "?accessToken=";
        var userPassengerMessage = ownerName + " has added you as a passenger to their ride.";
        for (var passenger : ride.getPassengers()) {
            try {
                if (passenger.getUser() == null) {
                    emailService.sendEmail(
                            passenger.getEmail(),
                            "New ride",
                            emailBodyGeneratorService.generatePassengerAddedEmail(
                                    ownerName,
                                    url + passenger.getAccessToken()
                            )
                    );
                }
                else {
                    if (passenger.getUser().getId().equals(ride.getRideOwner().getId())) {
                        // Driver doesn't need notification about new ride
                        continue;
                    }
                    notificationsService.sendNotificationToUser(passenger.getUser(),
                            "New ride",
                            userPassengerMessage,
                            "/rides/"+ride.getId()
                    );
                }

            }
            catch (Exception e) {
                //throw new RuntimeException(e);
            }
        }
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void statusChange(RideStatusChangedEvent event) {
        var ride = event.getRide();
        var ownerName = ride.getRideOwner().getFirstName() + " " + ride.getRideOwner().getLastName();
        var url = frontendUrl + "/rides/" + ride.getId() + "?accessToken=";
        var title = "Ride status changed";
        var message = "The status of your ride with " + ownerName + " has changed to " + ride.getStatus().name().toUpperCase();
        var ownerMessage = "The status of your ride has changed to " + ride.getStatus().name().toUpperCase();
        for (var passenger : ride.getPassengers()) {
            try {
                if (passenger.getUser() == null) {
                    emailService.sendEmail(
                            passenger.getEmail(),
                            "Ride status change",
                            emailBodyGeneratorService.generateRideStatusChangeEvent(
                                    ownerName,
                                    url + passenger.getAccessToken(),
                                    ride.getStatus().name().toUpperCase()
                            )
                    );
                } else {
                    if (passenger.getUser().getId().equals(ride.getRideOwner().getId())) {
                        notificationsService.sendNotificationToUser(passenger.getUser(),
                                "Ride status change",
                                ownerMessage,
                                "/rides/" + ride.getId()
                        );
                        continue;
                    }
                    notificationsService.sendNotificationToUser(passenger.getUser(),
                            "Ride status change",
                            message,
                            "/rides/" + ride.getId()
                    );
                }
            } catch (Exception e) {
                // e.printStackTrace();
            }
        }
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void scheduleDriverLookup(RideCreatedEvent event) {
        var ride = event.getRide();
        if (ride.getStatus() == RideStatus.PENDING) {
            this.scheduleDriverLookup(ride);
        }
    }


    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void scheduleNotificationsForNewRide(RideCreatedEvent event) {
        scheduleNotifications(event.getRide());
    }

    @EventListener(ApplicationReadyEvent.class)
    public void rescheduleExistingRides() {
        var rides = rideRepository.findFutureScheduledRides();
        rides.forEach(this::scheduleNotifications);
        rides.forEach(this::scheduleDriverLookup);
    }

    public void scheduleNotifications(Ride ride) {
        if (ride.getScheduledTime() == null) {
            return;
        }
        for (var minutesBefore : new Long[]{15L, 10L, 5L}) {
            var notificationTime = ride.getScheduledTime().minusMinutes(minutesBefore);
            if (notificationTime.isAfter(LocalDateTime.now())) {
                schedulingService.scheduleTask(
                        notificationTime,
                        () -> rideNotificationService
                                .sendNotifications(
                                        ride.getId(),
                                        minutesBefore
                                )
                );
            }
        }
    }

    public void scheduleDriverLookup(Ride ride) {
        if (ride.getScheduledTime() == null) {
            return;
        }
        for (var minutesBefore : new Long[]{20L, 15L, 10L, 5L, 0L}) {
            var notificationTime = ride.getScheduledTime().minusMinutes(minutesBefore);
            if (notificationTime.isAfter(LocalDateTime.now())) {
                schedulingService.scheduleTask(
                        notificationTime,
                        () -> rideBookingService
                                .findDriverForScheduledRide(
                                        ride.getId(),
                                        minutesBefore
                                )
                );
            }
        }
    }
}
