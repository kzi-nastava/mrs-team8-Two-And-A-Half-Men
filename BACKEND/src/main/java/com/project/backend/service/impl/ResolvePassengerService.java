package com.project.backend.service.impl;

import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.UnauthorizedException;
import com.project.backend.models.Passenger;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.PassengerRepository;
import com.project.backend.models.actor.AccessTokenPassengerActor;
import com.project.backend.models.actor.JwtPassengerActor;
import com.project.backend.models.actor.PassengerActor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResolvePassengerService {
    private final PassengerRepository passengerRepository;

    public Passenger resolveActor(PassengerActor actor, Ride ride) {
        if (actor instanceof JwtPassengerActor jwt) {
            return passengerRepository
                    .findByUserAndRide(jwt.customer(), ride)
                    .orElseThrow(() ->
                            new BadRequestException("Customer is not a passenger on this ride"));
        }

        if (actor instanceof AccessTokenPassengerActor token) {
            Passenger passenger = passengerRepository
                    .findByAccessToken(token.accessToken())
                    .orElseThrow(() ->
                            new UnauthorizedException("Invalid access token"));

            if (!passenger.getRide().getId().equals(ride.getId())) {
                throw new UnauthorizedException("Token does not match ride");
            }

            return passenger;
        }

        throw new ForbiddenException("Unknown rating actor");
    }

    public Passenger resolveActorOnActiveRide(PassengerActor actor) {
        if (actor instanceof JwtPassengerActor jwt) {
            var passenger = passengerRepository.findByCustomerWithRideStatus(jwt.customer(), List.of(RideStatus.ACTIVE));

            return passengerRepository
                    .findByUserAndRideStatusIn(jwt.customer(), List.of(RideStatus.ACTIVE))
                    .orElseThrow(() ->
                            new BadRequestException("Customer is not a passenger on this ride"));
        }

        if (actor instanceof AccessTokenPassengerActor token) {
            return passengerRepository
                    .findByAccessToken(token.accessToken())
                    .orElseThrow(() ->
                            new UnauthorizedException("Invalid access token"));
        }

        throw new ForbiddenException("Unknown rating actor");
    }
}
