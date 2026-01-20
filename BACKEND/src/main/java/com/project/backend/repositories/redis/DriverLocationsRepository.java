package com.project.backend.repositories.redis;

import com.project.backend.DTO.redis.RedisLocationsDTO;
import org.springframework.data.geo.Circle;
import org.springframework.data.geo.GeoResults;
import org.springframework.data.geo.Point;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class DriverLocationsRepository {
    private static final String GEO_KEY = "driver_locations";

    private final StringRedisTemplate redisTemplate;

    public DriverLocationsRepository(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void setLocations(Long driverID, double longitude, double latitude) {
        redisTemplate.opsForGeo().add(GEO_KEY, new RedisGeoCommands.GeoLocation<>(
                driverID.toString(),
                new Point(longitude, latitude)
        ));


    }
    public void setLocations(RedisLocationsDTO location) {
        redisTemplate.opsForGeo().add(GEO_KEY, new RedisGeoCommands.GeoLocation<>(
                location.getId().toString(),
                new Point(location.getLongitude(), location.getLatitude())
        ));
    }
    public void deactivateLocation(Long driverID) {
        redisTemplate.opsForGeo().remove(GEO_KEY, driverID.toString());
    }
    public Point getLocation(Long driverID) {
        List<Point> points = redisTemplate.opsForGeo()
                .position(GEO_KEY, driverID.toString());

        return (points == null || points.isEmpty()) ? null : points.get(0);
    }
    public List<RedisLocationsDTO> getAllLocations() {
        return getLocationsWithinRadius(0, 0, 20015); // Approximate radius of the Earth in kilometers
    }
    public List<RedisLocationsDTO> getLocationsWithinRadius(double longitude, double latitude, double radiusInKm) {
        GeoResults<RedisGeoCommands.GeoLocation<String>> results =
                redisTemplate.opsForGeo().radius(
                        GEO_KEY,
                        new Circle(
                                new Point(longitude, latitude),
                                new org.springframework.data.geo.Distance(radiusInKm, org.springframework.data.geo.Metrics.KILOMETERS)
                        ),
                        RedisGeoCommands.GeoRadiusCommandArgs.newGeoRadiusArgs().includeCoordinates()
                );

        if (results == null) {
            return new ArrayList<>();
        }

        return results.getContent()
                .stream()
                .map(result -> new RedisLocationsDTO(
                        Long.parseLong(result.getContent().getName()),
                        result.getContent().getPoint().getY(),
                        result.getContent().getPoint().getX()
                ))
                .toList();
    }

}
