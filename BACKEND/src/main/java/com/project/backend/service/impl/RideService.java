package com.project.backend.service.impl;

import com.project.backend.DTO.Ride.NoteRequestDTO;
import com.project.backend.DTO.Ride.NoteResponseDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.DTO.Ride.RideTrackingDTO;
import com.project.backend.DTO.mappers.RideMapper;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Customer;
import com.project.backend.models.Driver;
import com.project.backend.models.Passenger;
import com.project.backend.models.Ride;
import com.project.backend.models.enums.RideStatus;
import com.project.backend.repositories.CustomerRepository;
import com.project.backend.repositories.DriverRepository;
import com.project.backend.repositories.PassengerRepository;
import com.project.backend.repositories.RideRepository;
import com.project.backend.service.IRideService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RideService implements IRideService {
    private final RideRepository rideRepository;
    private final DriverRepository driverRepository;
    private final CustomerRepository customerRepository;
    private final PassengerRepository passengerRepository;

    private final SimpMessagingTemplate messagingTemplate;

    public RideResponseDTO getRideById(Long id) {
        Ride ride = rideRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Ride with id " + id + " not found"
                        ));

        return RideMapper.convertToHistoryResponseDTO(ride);
    }

    public RideResponseDTO getActiveRideByDriverId(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Driver with id " + id + " not found"));

        Ride activeRide = rideRepository
                .findFirstByDriverAndStatusIn(driver, List.of(RideStatus.ACCEPTED, RideStatus.ACTIVE))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active ride for driver with id " + id + " not found"
                        ));

        return RideMapper.convertToHistoryResponseDTO(activeRide);
    }

    public RideResponseDTO getActiveRideByCustomerId(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Customer with id " + id + " not found"));

        Ride activeRide = rideRepository
                .findFirstByRideOwnerAndStatusIn(customer, List.of(RideStatus.ACTIVE))
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Active ride for customer with id " + id + " not found"
                        ));

        return RideMapper.convertToHistoryResponseDTO(activeRide);
    }

    public NoteResponseDTO saveRideNote(Long rideId, Long passengerId, NoteRequestDTO noteRequest) {
        Passenger passenger = passengerRepository.findById(passengerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Passenger with id " + passengerId + " not found"));

        String noteText = noteRequest.getNoteText();
        if (noteText.isEmpty() || noteText.isBlank() || noteText.length() > 500)
            throw new BadRequestException("Note text length must be between 1 and 500 characters");

        Ride activeRide = rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Active ride with id " + rideId + " not found"));

        passenger.setInconsistencyNote(noteText);
        passengerRepository.save(passenger);

        return NoteResponseDTO.builder()
                .noteText(noteRequest.getNoteText())
                .passengerMail(passenger.getEmail())
                .rideId(activeRide.getId())
                .build();
    }

    public RideTrackingDTO getRideTrackingInfo(Long rideId) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Ride with id " + rideId + " not found"));

        return RideTrackingDTO.builder()
                .id(ride.getId())
                .stops(List.of("a")) // TODO: PETAR API
                .estimatedDistance(123D) // TODO: PETAR ESTIMATE
                .estimatedDuration(123)
                .status(ride.getStatus())
                .startTime(ride.getStartTime())
                .build();
    }

    public void sendRideUpdate(Ride ride) {
        RideTrackingDTO rideTrackingDTO = RideTrackingDTO.builder()
                .id(ride.getId())
                .stops(List.of("a")) // TODO: PETAR API
                .estimatedDistance(123D) // TODO: PETAR ESTIMATE
                .estimatedDuration(123)
                .status(ride.getStatus())
                .startTime(ride.getStartTime())
                .build();

        messagingTemplate.convertAndSend(
                "/topic/rides/" + ride.getId(),
                rideTrackingDTO
        );
    }
}
