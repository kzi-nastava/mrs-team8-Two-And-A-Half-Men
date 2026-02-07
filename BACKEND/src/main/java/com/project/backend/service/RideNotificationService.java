package com.project.backend.service;

import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideNotificationService {

    private final RideRepository rideRepository;
    private final EmailService emailService;
    private final EmailBodyGeneratorService emailBodyGeneratorService;

    @Value("${frontend.url}") private String frontendUrl;

    @Transactional(readOnly = true)
    public void sendNotifications(Long rideId, Long minutesBefore) {
        System.out.println("SENDING NOTIFICATIONS FOR RIDE " + rideId + " MINUTES BEFORE: " + minutesBefore);
        var ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return;
        }
        if (!List.of(RideStatus.PENDING, RideStatus.ACCEPTED).contains(ride.getStatus())) {
            return;
        }
        var ownerName = ride.getRideOwner().getFirstName() + " " + ride.getRideOwner().getLastName();
        var url = frontendUrl + "/ride/" + ride.getId() + "/track?accessToken=";
        for (var passenger : ride.getPassengers()) {
            try {
                if (passenger.getUser() == null) {
                    emailService.sendEmail(
                            passenger.getEmail(),
                            "Your ride is starting soon",
                            emailBodyGeneratorService.generateRideStartingSoonEmail(
                                    ownerName,
                                    url + passenger.getAccessToken(),
                                    minutesBefore
                            )
                    );
                }
                else {
                    // TODO: send push notification
                }
            }
            catch (Exception e) {
                //throw new RuntimeException(e);
            }
        }
    }

}
