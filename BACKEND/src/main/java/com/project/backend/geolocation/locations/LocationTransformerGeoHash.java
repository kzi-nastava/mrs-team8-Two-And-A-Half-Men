package com.project.backend.geolocation.locations;

import ch.hsr.geohash.GeoHash;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;

import java.util.ArrayList;
import java.util.List;

public class LocationTransformerGeoHash extends LocationTransformer {

    public int length;
    public LocationTransformerGeoHash(CoordinatesFactory factory, int length) {
        super(factory);
        this.length = length;
    }

    @Override
    public String transformLocation(List<Coordinates> locations) {
        StringBuilder hashBuilder = new StringBuilder();
        for (Coordinates location : locations) {
            System.out.println(location.getLatitude());
            GeoHash geoHash = GeoHash.withCharacterPrecision(location.getLatitude(), location.getLongitude(), this.length);
            String hash = geoHash.toBase32();
            hashBuilder.append(hash);
        }
        return hashBuilder.toString() ;
    }

    @Override
    public List<Coordinates> transformToCoordinates(String hash) {
        List<Coordinates> coordinates = new ArrayList<>();
        for (int i = 0; i < hash.length(); i += this.length) {
            String base = hash.substring(i, i + this.length);
            System.out.println(base);
            try {
                GeoHash geoHash = GeoHash.fromGeohashString(base);
                coordinates.add(factory.getCoordinate(geoHash.getOriginatingPoint().getLatitude(), geoHash.getOriginatingPoint().getLongitude()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return coordinates;
    }
}
