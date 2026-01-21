package com.project.backend.geolocation.locations;

import ch.hsr.geohash.GeoHash;
import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;

import java.util.ArrayList;
import java.util.List;

public class LocationTransformerGeoHashCompressed  extends LocationTransformer{

    private final int length;
    public LocationTransformerGeoHashCompressed(CoordinatesFactory factory, int length) {
        super(factory);
        this.length = length;
    }


    @Override
    public String transformLocation(List<Coordinates> locations) {
        StringBuilder hashBuilder = new StringBuilder();
        String startingHash = null;
        boolean isFirst = true;
        for (Coordinates location : locations) {
            //System.out.println(location.getLatitude());
            GeoHash geoHash = GeoHash.withCharacterPrecision(location.getLatitude(), location.getLongitude(), this.length);
            String hash = geoHash.toBase32();
            if (isFirst) {
                startingHash = hash;
                isFirst = false;
            } else {
                int commonPrefixLength = 0;
                for (int i = 0; i < this.length; i++) {
                    if (startingHash.charAt(i) == hash.charAt(i)) {
                        commonPrefixLength++;
                    } else {
                        break;
                    }
                }
                if (commonPrefixLength > 1) {
                    hashBuilder.append("|");
                    hashBuilder.append(commonPrefixLength);
                    hash = hash.substring(commonPrefixLength);
                }
            }
            hashBuilder.append(hash);
        }
        return hashBuilder.toString();
    }

    @Override
    public List<Coordinates> transformToCoordinates(String hash) {
        List<Coordinates> coordinates = new ArrayList<>();
        int index = 0;
        String startingHash = hash.substring(0, this.length);
        try { //Find a way how this could work faster
            GeoHash geoHash = GeoHash.fromGeohashString(startingHash);
            coordinates.add(factory.getCoordinate(geoHash.getOriginatingPoint().getLatitude(), geoHash.getOriginatingPoint().getLongitude()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        index += this.length;
        while (index < hash.length()){
            int commonPrefixLength = 0;
            char isCompressed = hash.charAt(index);
            if(isCompressed == '|') {
                index++;
                commonPrefixLength = Character.getNumericValue(hash.charAt(index));
                index++;
            }
            String base = startingHash.substring(0, commonPrefixLength) + hash.substring(index, index + this.length - commonPrefixLength);
            index += this.length - commonPrefixLength;
            startingHash = base;
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
