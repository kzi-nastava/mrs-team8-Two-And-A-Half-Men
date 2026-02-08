package com.project.backend.DTO.mappers;

import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.models.Location;
import com.project.backend.models.Ride;
import com.project.backend.models.AdditionalService;
import com.project.backend.models.Passenger;
import com.project.backend.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RideMapper {
    private final LocationTransformer locationTransformer;
    private final LocationRepository locationRepository;

    public RideResponseDTO convertToRideResponseDTO(Ride ride) {

        List<String> additionalServices = ride.getAdditionalServices()
                .stream()
                .map(AdditionalService::getName)
                .toList();

        List<Coordinates> coordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash());
        List<String> hashes = coordinates
                .stream().map(
                        c -> locationTransformer
                                .transformFromPoints(List.of(new double[] {c.getLatitude(), c.getLongitude()}))
                ).toList();
        var locations = locationRepository.findAllByGeoHashIn(hashes);

        List<String> addresses = locations.stream().map(
                Location::getAddress
        ).toList();

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

