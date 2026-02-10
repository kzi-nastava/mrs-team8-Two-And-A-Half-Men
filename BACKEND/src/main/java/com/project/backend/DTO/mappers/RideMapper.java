package com.project.backend.DTO.mappers;

import com.project.backend.DTO.LocationDTO;
import com.project.backend.DTO.PassengerDTO;
import com.project.backend.DTO.Ride.RideResponseDTO;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.models.Ride;
import com.project.backend.models.AdditionalService;
import com.project.backend.repositories.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class RideMapper {
    private final LocationTransformer locationTransformer;
    private final LocationRepository locationRepository;

    public RideResponseDTO convertToRideResponseDTO(Ride ride) {

        // Additional services
        List<String> additionalServices = ride.getAdditionalServices()
                .stream()
                .map(AdditionalService::getName)
                .toList();

        // Passengers
        List<PassengerDTO> passengers = ride.getPassengers()
                .stream().map(
                        p -> PassengerDTO.builder()
                                .email(p.getEmail() != null ? p.getEmail() : p.getUser().getEmail())
                                .inconsistencyNote(p.getInconsistencyNote())
                                .driverRating(p.getDriverRating())
                                .vehicleRating(p.getVehicleRating())
                                .comment(p.getComment())
                                .build()
                ).toList();

        // Locations
        List<Coordinates> coordinates = locationTransformer.transformToCoordinates(ride.getRoute().getGeoHash());
        List<String> hashes = coordinates
                .stream().map(
                        c -> locationTransformer
                                .transformFromPoints(List.of(new double[] {c.getLatitude(), c.getLongitude()}))
                ).toList();
        List<LocationDTO> locations = locationRepository.findAllByGeoHashIn(hashes)
                .stream().map(
                        l -> LocationDTO.builder()
                                .geoHash(l.getGeoHash())
                                .latitude(l.getLatitude())
                                .longitude(l.getLongitude())
                                .address(l.getAddress())
                                .build()
                ).toList();

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
                .passengers(passengers)
                .locations(locations)

                .build();
    }
}

