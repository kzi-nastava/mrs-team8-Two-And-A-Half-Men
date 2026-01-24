package com.project.backend.geolocation.locations;

import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;
import com.project.backend.geolocation.metrics.MetricsDistance;
import com.project.backend.geolocation.metrics.MetricsTime;
import org.springframework.data.geo.Point;

import java.util.ArrayList;
import java.util.List;

public abstract class LocationTransformer {
    public LocationTransformer(CoordinatesFactory factory) {
        this.factory = factory;
    }

    protected CoordinatesFactory factory;
    public abstract String transformLocation(List<Coordinates> locations);
    public String transformAddress(List<String> addresses) {
        StringBuilder hashBuilder = new StringBuilder();
        List<Coordinates> locations = new ArrayList<>();
        for (String address : addresses) {
            locations.add(factory.getCoordinate(address));
        }
        return transformLocation(locations);
    }
    public String transformFromPoints(List<double[]> points) {
        StringBuilder hashBuilder = new StringBuilder();
        List<Coordinates> locations = new ArrayList<>();
        for (double[] point : points) {
            locations.add(factory.getCoordinate(point[0], point[1]));
        }
        return transformLocation(locations);
    }

    public abstract List<Coordinates> transformToCoordinates(String hash);
    public List<String> transformToAddress(String hash) {
        List<Coordinates> cords = transformToCoordinates(hash);
        List<String> addresses = new ArrayList<>();
        for (Coordinates coordinate : cords) {
            addresses.add(coordinate.getAddress());
        }
        return addresses;

    }


    //Returns in meters
    public double calculateDistanceAir(String hash) {
        List<Coordinates> cords = transformToCoordinates(hash);
        double distance = 0.0;
        Coordinates previousCoordinate = null;
        for (Coordinates coordinate : cords) {
            if (previousCoordinate != null) {
                distance += coordinate.distanceAirLine(previousCoordinate);
            }
            previousCoordinate = coordinate;
        }
        return distance;
    }
    public double calculateDistanceAir(List<String> hashCodes) {
        double distance = 0.0;
        for( String hash : hashCodes) {
            List<Coordinates> cords = transformToCoordinates(hash);
            Coordinates previousCoordinate = null;
            for (Coordinates coordinate : cords) {
                if (previousCoordinate != null) {
                    distance += coordinate.distanceAirLine(previousCoordinate);
                }
                previousCoordinate = coordinate;
            }
        }
        return distance;
    }
    //Returns in specified metric
    public double calculateDistance(String hash, MetricsDistance metric) {
        return metric.fromMeters(calculateDistanceAir(hash));
    }
    public double calculateDistance(List<String> hashCodes, MetricsDistance metric) {
        return metric.fromMeters(calculateDistanceAir(hashCodes));
    }
    //Returns in meters
    public double calculateDistanceMap(String hash) {
        List<Coordinates> cords = transformToCoordinates(hash);
        double distance = 0.0;
        Coordinates previousCoordinate = null;
        for (Coordinates coordinate : cords) {
            if (previousCoordinate != null) {
                distance += previousCoordinate.GetDistanceMap(coordinate);
            }
            previousCoordinate = coordinate;
        }
        return distance;
    }
    public double calculateDistanceMap(List<String> hashCodes) {
        double distance = 0.0;
        for (String hash : hashCodes) {
            List<Coordinates> cords = transformToCoordinates(hash);
            Coordinates previousCoordinate = null;
            for (Coordinates coordinate : cords) {
                if (previousCoordinate != null) {
                    distance += coordinate.GetDistanceMap(previousCoordinate);
                }
                previousCoordinate = coordinate;
            }
        }
        return distance;
    }
    //Returns in specified metric
    public double calculateDistanceMap(String hash, MetricsDistance metric) {
        return metric.fromMeters(calculateDistanceMap(hash));
    }
    public double calculateDistanceMap(List<String> hashCodes, MetricsDistance metric) {
        return metric.fromMeters(calculateDistanceMap(hashCodes));
    }
    public double calculateTimeMap(String hash) {
        List<Coordinates> cords = transformToCoordinates(hash);
        double time = 0.0;
        Coordinates previousCoordinate = null;
        for (Coordinates coordinate : cords) {
            if (previousCoordinate != null) {
                time += previousCoordinate.GetTimeMap(coordinate);
            }
            previousCoordinate = coordinate;
        }
        return time;
    }
    public double calculateTimeMap(List<String> hashCodes) {
        double time = 0.0;
        for (String hash : hashCodes) {
            List<Coordinates> cords = transformToCoordinates(hash);
            Coordinates previousCoordinate = null;
            for (Coordinates coordinate : cords) {
                if (previousCoordinate != null) {
                    time += coordinate.GetTimeMap(previousCoordinate);
                }
                previousCoordinate = coordinate;
            }
        }
        return time;
    }
    public double calculateTimeMap(String hash, MetricsTime metric) {
        return metric.fromSeconds(calculateTimeMap(hash));
    }
    public double calculateTimeMap(List<String> hashCodes, MetricsTime metric) {
        return metric.fromSeconds(calculateTimeMap(hashCodes));
    }
    public double[] calculateRouteMap(String hash) {
        List<Coordinates> cords = transformToCoordinates(hash);
        double totalDistance = 0.0;
        double totalTime = 0.0;
        Coordinates previousCoordinate = null;
        for (Coordinates coordinate : cords) {
            if (previousCoordinate != null) {
                totalDistance += previousCoordinate.GetDistanceMap(coordinate);
                totalTime += previousCoordinate.GetTimeMap(coordinate);
            }
            previousCoordinate = coordinate;
        }
        return new double[] { totalDistance, totalTime };
    }
    public double[] calculateRouteMap(List<String> hashCodes) {
        double totalDistance = 0.0;
        double totalTime = 0.0;
        for (String hash : hashCodes) {
            List<Coordinates> cords = transformToCoordinates(hash);
            Coordinates previousCoordinate = null;
            for (Coordinates coordinate : cords) {
                if (previousCoordinate != null) {
                    totalDistance += coordinate.GetDistanceMap(previousCoordinate);
                    totalTime += coordinate.GetTimeMap(previousCoordinate);
                }
                previousCoordinate = coordinate;
            }
        }
        return new double[] { totalDistance, totalTime };
    }
    public double[] calculateRouteMap(String hash, MetricsDistance distanceMetric, MetricsTime timeMetric) {
        double[] route = calculateRouteMap(hash);
        return new double[] { distanceMetric.fromMeters(route[0]), timeMetric.fromSeconds(route[1]) };
    }
    public double[] calculateRouteMap(List<String> hashCodes, MetricsDistance distanceMetric, MetricsTime timeMetric) {
        double[] route = calculateRouteMap(hashCodes);
        return new double[] { distanceMetric.fromMeters(route[0]), timeMetric.fromSeconds(route[1]) };
    }
    public String addCoordinateToHash(String hash, Coordinates coordinate) {
        List<Coordinates> cords = transformToCoordinates(hash);
        cords.add(coordinate);
        return transformLocation(cords);
    }
    public String addAddressToHash(String hash, String address) {
        List<Coordinates> cords = transformToCoordinates(hash);
        cords.add(factory.getCoordinate(address));
        return transformLocation(cords);
    }
    public String addPointToHash(String hash, double longitude, double latitude) {
        List<Coordinates> cords = transformToCoordinates(hash);
        cords.add(factory.getCoordinate(latitude, longitude));
        return transformLocation(cords);
    }
    public String addPointToHash(String hash, Point point) {
        List<Coordinates> cords = transformToCoordinates(hash);
        cords.add(factory.getCoordinate(point.getY(), point.getX()));
        return transformLocation(cords);
    }
}
