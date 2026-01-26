package com.project.backend.service.rating;

import com.project.backend.DTO.Ride.RatingRequestDTO;
import com.project.backend.DTO.Ride.RatingResponseDTO;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ForbiddenException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.exceptions.UnauthorizedException;
import com.project.backend.models.Passenger;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.PassengerRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IRatingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final PassengerRepository passengerRepository;
    private final RideRepository rideRepository;

    private static final int RATING_DEADLINE_DAYS = 3;

    @Transactional
    public RatingResponseDTO rateRide(
            Long rideId,
            RatingActor actor,
            RatingRequestDTO request
    ) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResourceNotFoundException("Ride with id " + rideId + " not found"));

        Passenger passenger = resolvePassenger(actor, ride);

        if (ride.getStatus() != RideStatus.FINISHED && ride.getStatus() != RideStatus.INTERRUPTED)
            throw new BadRequestException("Cannot rate ride that is not finished");

        if (!canRate(passenger, ride))
            throw new BadRequestException("Rating deadline has passed. You had 3 days from ride completion.");

        if (isRatedAlready(passenger))
            throw new BadRequestException("Ride is already rated.");

        passenger.setVehicleRating(request.getVehicleRating());
        passenger.setDriverRating(request.getDriverRating());
        passenger.setComment(request.getComment());

        Passenger saved = passengerRepository.save(passenger);

        return convertToRatingResponseDTO(saved);
    }

    private Passenger resolvePassenger(RatingActor actor, Ride ride) {

        if (actor instanceof JwtRatingActor jwt) {
            return passengerRepository
                    .findByUserAndRide(jwt.customer(), ride)
                    .orElseThrow(() ->
                            new BadRequestException("Customer is not a passenger on this ride"));
        }

        if (actor instanceof AccessTokenRatingActor token) {
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


    private boolean isRatedAlready(Passenger passenger) {
        return passenger.getVehicleRating() != null
                && passenger.getDriverRating() != null
                && passenger.getComment() != null;
    }

    private boolean canRate(Passenger passenger, Ride ride) {
        if (passenger.getVehicleRating() != null && passenger.getDriverRating() != null)
            return false;

        LocalDateTime deadline = ride.getEndTime().plusDays(RATING_DEADLINE_DAYS);
        return !LocalDateTime.now().isAfter(deadline);
    }

    private RatingResponseDTO convertToRatingResponseDTO(Passenger passenger) {
        return new RatingResponseDTO(
                passenger.getId(),
                passenger.getRide().getId(),
                passenger.getVehicleRating(),
                passenger.getDriverRating(),
                passenger.getComment()
        );
    }
}
