package com.project.backend.service.impl;

import com.project.backend.DTO.RatingRequestDTO;
import com.project.backend.DTO.RatingResponseDTO;
import com.project.backend.models.Passenger;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.PassengerRepository;
import com.project.backend.service.IRatingService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RatingService implements IRatingService {
    private final PassengerRepository passengerRepository;

    private static final int RATING_DEADLINE_DAYS = 3;

    @Transactional
    public RatingResponseDTO rateRide(RatingRequestDTO request) throws ChangeSetPersister.NotFoundException, BadRequestException {
        Passenger passenger = passengerRepository
                        .findById(request.getPassengerId())
                        .orElseThrow(ChangeSetPersister.NotFoundException::new);

        Ride ride = passenger.getRide();
        if (ride.getStatus() != RideStatus.FINISHED && ride.getStatus() != RideStatus.INTERRUPTED) {
            throw new BadRequestException("Cannot rate ride that is not finished");
        }

        if (!canRate(passenger, ride)) {
            throw new BadRequestException("Rating deadline has passed. You had 3 days from ride completion.");
        }

        if (!isRatedAlready(passenger)) {
            throw new BadRequestException("Ride is already rated.");
        }

        passenger.setVehicleRating(request.getVehicleRating());
        passenger.setDriverRating(request.getDriverRating());
        passenger.setComment(request.getComment());

        Passenger saved = passengerRepository.save(passenger);

        return convertToRatingResponseDTO(saved);
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
