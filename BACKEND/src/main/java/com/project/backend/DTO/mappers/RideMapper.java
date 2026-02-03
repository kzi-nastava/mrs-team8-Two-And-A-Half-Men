package com.project.backend.DTO.mappers;

import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.models.Ride;
import com.project.backend.models.AdditionalService;
import com.project.backend.models.Passenger;

import java.util.ArrayList;
import java.util.List;

public class RideMapper {

    public static RideResponseDTO convertToRideResponseDTO(Ride ride) {

        List<String> additionalServices = ride.getAdditionalServices()
                .stream()
                .map(AdditionalService::getName)
                .toList();

        // TODO: waiting for geoHash api
        List<String> addresses = new ArrayList<>();

        List<String> passengersMails = ride.getPassengers()
                .stream()
                .map(Passenger::getEmail)
                .toList();

        return RideResponseDTO.builder()
                .id(ride.getId())

                .startTime(ride.getStartTime())
                .endTime(ride.getEndTime())
                .scheduledTime(ride.getScheduledTime())

                .driverName(ride.getDriver().firstNameAndLastName())
                .rideOwnerName(ride.getRideOwner().firstNameAndLastName())

                .status(ride.getStatus())
                .path(ride.getPath())
                .cancellationReason(ride.getCancellationReason())
                .price(ride.getPrice())
                .totalCost(ride.getTotalCost())

                .additionalServices(additionalServices)
                .passengersMails(passengersMails)
                .addresses(addresses)

                .build();
    }
}

