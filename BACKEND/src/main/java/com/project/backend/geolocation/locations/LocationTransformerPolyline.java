package com.project.backend.geolocation.locations;

import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;

import java.util.ArrayList;

public class LocationTransformerPolyline extends LocationTransformer {
    private int nextIndex = 0;
    private final double precision;
    public LocationTransformerPolyline(CoordinatesFactory factory, double precision) {
        super(factory);
        this.precision = precision;
    }

    private void encodeNumber(int num, StringBuilder encoded) {
        num = num << 1;
        if (num < 0) {
            num = ~num;
        }
        while (num >= 0x20) {
            int nextValue = (0b00100000 | (num & 0b00011111)) + 63;
            encoded.append((char) (nextValue));
            num >>= 5;
        }
        num += 63;
        encoded.append((char) (num));
    }


    @Override
    public String transformLocation(ArrayList<Coordinates> locations) {
        long lastLat = 0;
        long lastLon = 0;
        StringBuilder encoded = new StringBuilder();
        for (Coordinates location : locations) {
            long lat = Math.round(location.getLatitude() * this.precision);
            long lon = Math.round(location.getLongitude() * this.precision);
            long dLat = lat - lastLat;
            long dLon = lon - lastLon;
            encodeNumber((int) dLat, encoded);
            encodeNumber((int) dLon, encoded);
            lastLat = lat;
            lastLon = lon;
        }
        return encoded.toString();
    }

    @Override
    public ArrayList<Coordinates> transformToCoordinates(String hash) {
        ArrayList<Coordinates> locations = new ArrayList<>();
        long lat = 0;
        long lon = 0;
        int index = 0;
        while (index < hash.length()) {
            long dLat = decodeNumber(hash, index);
            index = nextIndex;
            long dLon = decodeNumber(hash, index);
            index = nextIndex;
            lat += dLat;
            lon += dLon;
            locations.add(factory.getCoordinate(lat / this.precision, lon / this.precision));
        }
        return locations;
    }

    private long decodeNumber(String hash, int index) {
        long result = 0;
        int shift = 0;
        int b;
        nextIndex = index;
        do {
            b = hash.charAt(nextIndex++) - 63;
            result |= (long)(b & 0b00011111) << shift;
            shift += 5;
        } while (b >= 0b00100000);
        return ((result & 1) != 0) ? ~(result >> 1) : (result >> 1);
    }
}
