package com.project.backend.geolocation.locations;

import com.project.backend.geolocation.coordinates.Coordinates;
import com.project.backend.geolocation.coordinates.CoordinatesFactory;
import com.project.backend.geolocation.metrics.MetricsDistance;
import com.project.backend.geolocation.metrics.MetricsTime;

import java.util.ArrayList;

public abstract class LocationTransformer {
    public LocationTransformer(CoordinatesFactory factory) {
        this.factory = factory;
    }

    protected CoordinatesFactory factory;
    public abstract String transformLocation(ArrayList<Coordinates> locations);
    public String transformAddress(ArrayList<String> addresses) {
        StringBuilder hashBuilder = new StringBuilder();
        ArrayList<Coordinates> locations = new ArrayList<>();
        for (String address : addresses) {
            locations.add(factory.getCoordinate(address));
        }
        return transformLocation(locations);
    }
    public String transformFromPoints(ArrayList<double[]> points) {
        StringBuilder hashBuilder = new StringBuilder();
        ArrayList<Coordinates> locations = new ArrayList<>();
        for (double[] point : points) {
            locations.add(factory.getCoordinate(point[0], point[1]));
        }
        return transformLocation(locations);
    }

    public abstract ArrayList<Coordinates> transformToCoordinates(String hash);
    public ArrayList<String> transformToAddress(String hash) {
        ArrayList<Coordinates> cords = transformToCoordinates(hash);
        ArrayList<String> addresses = new ArrayList<>();
        for (Coordinates coordinate : cords) {
            addresses.add(coordinate.getAddress());
        }
        return addresses;

    }


    //Returns in meters
    public double calculateDistanceAir(String hash) {
        ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double calculateDistanceAir(ArrayList<String> hashCodes) {
        double distance = 0.0;
        for( String hash : hashCodes) {
            ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double calculateDistance(ArrayList<String> hashCodes, MetricsDistance metric) {
        return metric.fromMeters(calculateDistanceAir(hashCodes));
    }
    //Returns in meters
    public double calculateDistanceMap(String hash) {
        ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double calculateDistanceMap(ArrayList<String> hashCodes) {
        double distance = 0.0;
        for (String hash : hashCodes) {
            ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double calculateDistanceMap(ArrayList<String> hashCodes, MetricsDistance metric) {
        return metric.fromMeters(calculateDistanceMap(hashCodes));
    }
    public double calculateTimeMap(String hash) {
        ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double calculateTimeMap(ArrayList<String> hashCodes) {
        double time = 0.0;
        for (String hash : hashCodes) {
            ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double calculateTimeMap(ArrayList<String> hashCodes, MetricsTime metric) {
        return metric.fromSeconds(calculateTimeMap(hashCodes));
    }
    public double[] calculateRouteMap(String hash) {
        ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double[] calculateRouteMap(ArrayList<String> hashCodes) {
        double totalDistance = 0.0;
        double totalTime = 0.0;
        for (String hash : hashCodes) {
            ArrayList<Coordinates> cords = transformToCoordinates(hash);
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
    public double[] calculateRouteMap(ArrayList<String> hashCodes, MetricsDistance distanceMetric, MetricsTime timeMetric) {
        double[] route = calculateRouteMap(hashCodes);
        return new double[] { distanceMetric.fromMeters(route[0]), timeMetric.fromSeconds(route[1]) };
    }
}
