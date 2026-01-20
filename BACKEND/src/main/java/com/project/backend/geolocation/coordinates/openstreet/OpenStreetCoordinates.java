package com.project.backend.geolocation.coordinates.openstreet;

import com.project.backend.geolocation.connectionproxy.HttpUrlConnectionProxy;
import com.project.backend.geolocation.coordinates.Coordinates;

import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class OpenStreetCoordinates extends Coordinates {

    private String adress = null;
    public OpenStreetCoordinates() {
        super();

    }
    public OpenStreetCoordinates(String address) {
        super(address);
    }
    public OpenStreetCoordinates(double latitude, double longitude) {
        super(latitude, longitude);
    }


    @Override
    public void setCoordinate(String address) {
        this.adress = address;
        try{
            String urlStr = "https://nominatim.openstreetmap.org/search?format=json&q="
                    + address.replace(" ", "+");
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) new HttpUrlConnectionProxy(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Java App");

            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null ) {
                response.append(line);
            }
            in.close();
            //System.out.println(response.toString());
            //This is realy slow way to parse JSON, but I want to avoid adding external libraries
            String jsonResponse = response.toString();
            String lat = jsonResponse.split("\"lat\":\"")[1].split("\"")[0];
            String lon = jsonResponse.split("\"lon\":\"")[1].split("\"")[0];
            //System.out.println("Latitude: " + lat + ", Longitude: " + lon);
            //Setting the values
            this.latitude = (double) Double.parseDouble(lat);
            this.longitude = (double) Double.parseDouble(lon);
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public String getAddress() {
        if (this.adress != null) {
            return this.adress;
        }
        try{
            String urlStr = "https://nominatim.openstreetmap.org/reverse?format=json&lat="
                    + getLatitude() + "&lon=" + getLongitude();
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) new HttpUrlConnectionProxy(url);
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Java App");

            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;

            while ((line = in.readLine()) != null ) {
                response.append(line);
            }
            in.close();

            String jsonResponse = response.toString();
            String address = jsonResponse.split("\"display_name\":\"")[1].split("\"")[0];
            this.adress = address;
            return address;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    public double GetDistanceMap(Coordinates other) {
        try{
            String urlStr = "https://router.project-osrm.org/route/v1/driving/"
                    + this.longitude + "," + this.latitude + ";"
                    + other.getLongitude() + "," + other.getLatitude()
                    + "?overview=false";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) new HttpUrlConnectionProxy(url);
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null ) {
                response.append(line);
            }
            in.close();
            String jsonResponse = response.toString();
            if(jsonResponse.contains("\"code\":\"NoRoute\"")){
                return -1;
            }
            int distanceIndex = jsonResponse.indexOf("\"distance\":");
            if (distanceIndex == -1) {
                return -1;
            }

            String afterDistance = jsonResponse.substring(distanceIndex + 11); // "distance": length
            int commaIndex = afterDistance.indexOf(",");
            int braceIndex = afterDistance.indexOf("}");
            int endIndex = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;

            if (endIndex == -1) {
                return -1;
            }

            String distanceStr = afterDistance.substring(0, endIndex).trim();
            return Double.parseDouble(distanceStr); // Convert to km
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }
    public double GetTimeMap(Coordinates other) {
        try{
            String urlStr = "https://router.project-osrm.org/route/v1/driving/"
                    + this.longitude + "," + this.latitude + ";"
                    + other.getLongitude() + "," + other.getLatitude()
                    + "?overview=false";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) new HttpUrlConnectionProxy(url);
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null ) {
                response.append(line);
            }
            in.close();
            String jsonResponse = response.toString();
            if(jsonResponse.contains("\"code\":\"NoRoute\"")){
                return -1;
            }
            int durationIndex = jsonResponse.indexOf("\"duration\":");
            if (durationIndex == -1) {
                return -1;
            }

            String afterDuration = jsonResponse.substring(durationIndex + 11); // "duration": length
            int commaIndex = afterDuration.indexOf(",");
            int braceIndex = afterDuration.indexOf("}");
            int endIndex = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;

            if (endIndex == -1) {
                return -1;
            }

            String durationStr = afterDuration.substring(0, endIndex).trim();
            return Double.parseDouble(durationStr); // Convert to km
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    @Override
    public double[] GetRouteMap(Coordinates other) {
        try{
            String urlStr = "https://router.project-osrm.org/route/v1/driving/"
                    + this.longitude + "," + this.latitude + ";"
                    + other.getLongitude() + "," + other.getLatitude()
                    + "?overview=false";
            URL url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) new HttpUrlConnectionProxy(url);
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new java.io.InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null ) {
                response.append(line);
            }
            in.close();
            String jsonResponse = response.toString();
            if(jsonResponse.contains("\"code\":\"NoRoute\"")){
                return null;
            }
            int distanceIndex = jsonResponse.indexOf("\"distance\":");
            if (distanceIndex == -1) {
                return null;
            }


            String afterDistance = jsonResponse.substring(distanceIndex + 11); // "distance": length
            int commaIndex = afterDistance.indexOf(",");
            int braceIndex = afterDistance.indexOf("}");
            int endIndex = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;
            if (endIndex == -1) {
                return null;
            }
            String distanceStr = afterDistance.substring(0, endIndex).trim();
            double distance = Double.parseDouble(distanceStr); // in meters
            int durationIndex = jsonResponse.indexOf("\"duration\":");
            if (durationIndex == -1) {
                return null;
            }

            String afterDuration = jsonResponse.substring(durationIndex + 11); // "duration": length
            commaIndex = afterDuration.indexOf(",");
            braceIndex = afterDuration.indexOf("}");
            endIndex = (commaIndex != -1 && commaIndex < braceIndex) ? commaIndex : braceIndex;

            if (endIndex == -1) {
                return null;
            }

            String durationStr = afterDuration.substring(0, endIndex).trim();
            double duration = Double.parseDouble(durationStr); // in seconds
            return new double[]{distance, duration};
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
