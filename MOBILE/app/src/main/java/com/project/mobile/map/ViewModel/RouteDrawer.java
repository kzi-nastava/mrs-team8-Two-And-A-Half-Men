package com.project.mobile.map.ViewModel;

import android.util.Log;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.NominatimResult;
import com.project.mobile.DTO.Route;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.Polyline;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class RouteDrawer extends ViewModel {
    private final MutableLiveData<List<Route>> routes =
            new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<Route>> getRoutes() {
        return routes;
    }

    private List<GeoPoint> getRoute(GeoPoint startPoint, GeoPoint endPoint) throws IOException, JSONException {
        String url = "https://router.project-osrm.org/route/v1/driving/" +
                startPoint.getLongitude() + "," + startPoint.getLatitude() + ";" +
                endPoint.getLongitude() + "," + endPoint.getLatitude() +
                "?overview=full&geometries=geojson";

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful() && response.body() != null) {
            String responseBody = response.body().string();
            JSONObject json = new JSONObject(responseBody);
            JSONArray coordinates = json.getJSONArray("routes")
                    .getJSONObject(0)
                    .getJSONObject("geometry")
                    .getJSONArray("coordinates");

            List<GeoPoint> routePoints = new ArrayList<>();
            for (int i = 0; i < coordinates.length(); i++) {
                JSONArray point = coordinates.getJSONArray(i);
                double lon = point.getDouble(0);
                double lat = point.getDouble(1);
                routePoints.add(new GeoPoint(lat, lon));
            }
            Log.d("RouteDrawer", "Route points retrieved: " + routePoints.size());
            return routePoints;
        }
        return null;
    }

    public void addRoute(List<NominatimResult> routePoints, int color, String name) {
        new Thread(() -> {
            removeRoute(name);
            List<GeoPoint> geoPoints = new ArrayList<>();
            for(int i = 0; i < routePoints.size() - 1; i++) {
                NominatimResult start = routePoints.get(i);
                NominatimResult end = routePoints.get(i + 1);
                GeoPoint startPoint = new GeoPoint(start.getLat(), start.getLon());
                GeoPoint endPoint = new GeoPoint(end.getLat(), end.getLon());
                try {
                    List<GeoPoint> segmentPoints = getRoute(startPoint, endPoint);
                    if (segmentPoints != null && !segmentPoints.isEmpty()) {
                        geoPoints.addAll(segmentPoints);
                        Log.d("RouteDrawer", "Segment " + i + " added: " + segmentPoints.size() + " points");
                    }
                    } catch (IOException | JSONException e) {
                    Log.e("RouteDrawer", "Error fetching route segment " + i, e);
                }
            }

            if (!geoPoints.isEmpty()) {
                Polyline routeLine = new Polyline();
                routeLine.setPoints(geoPoints);
                routeLine.setColor(color);
                routeLine.setWidth(10f);
                routeLine.getPaint().setStrokeCap(android.graphics.Paint.Cap.ROUND);

                Log.d("RouteDrawer", "Adding route '" + name + "' with " + geoPoints.size() + " points");
                addRoute(routeLine, name);
            } else {
                Log.e("RouteDrawer", "No route points fetched for route: " + name);
            }
        }).start();
    }

    private void addRoute(Polyline routeLine, String name) {
        List<Route> currentRoutes = routes.getValue();
        if (currentRoutes != null) {
            currentRoutes.add(new Route(routeLine, name));
            routes.postValue(currentRoutes);
        }
    }

    public void removeRoute(String name) {
        List<Route> currentRoutes = routes.getValue();
        if (currentRoutes != null) {
            currentRoutes.removeIf(route -> route.getName().equals(name));
            routes.postValue(currentRoutes);
        }
    }

    public void clearRoutes() {
        routes.postValue(new ArrayList<>());
    }

    public void startDrawingRoute(LifecycleOwner owner, LiveData<List<NominatimResult>> liveData, int color, String routeName) {
        liveData.observe(owner, dataList -> {
            if (dataList != null && dataList.size() >= 2) {
                addRoute(dataList, color, routeName);
            } else {
                removeRoute(routeName);
            }
        });
    }


}