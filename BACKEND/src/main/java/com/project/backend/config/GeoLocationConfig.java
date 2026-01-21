package com.project.backend.config;

import com.project.backend.geolocation.coordinates.openstreet.OpenStreetCoordinatesFactory;
import com.project.backend.geolocation.locations.LocationTransformer;
import com.project.backend.geolocation.locations.LocationTransformerGeoHash;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GeoLocationConfig {

    @Bean
    public OpenStreetCoordinatesFactory openStreetCoordinatesFactory() {
        return new OpenStreetCoordinatesFactory();
    }

    @Bean
    public LocationTransformer locationTransformer(
            OpenStreetCoordinatesFactory factory,
            @Value("${geo.location.geohash.length}") int length
    ) {
        return new LocationTransformerGeoHash(factory, length);
    }
}
