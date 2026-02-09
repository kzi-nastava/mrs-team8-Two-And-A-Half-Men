package com.project.mobile.map;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;

import com.project.mobile.DTO.Map.NominatimResult;
import com.project.mobile.R;
import com.project.mobile.map.ViewModel.MarkerDrawer;
import com.project.mobile.map.ViewModel.RouteDrawer;
import com.project.mobile.map.ViewModel.SheredLocationViewModel;

import java.io.File;

public class MapFragment extends Fragment {

    private SheredLocationViewModel sheredLocationViewModel;
    private RouteDrawer routeDrawer;
    private MapView mapView;
    private MarkerDrawer markerDrawer;

    private boolean isClicable = true;

    public MapFragment() {
    }
    public MapFragment(boolean isClicable) {
        this.isClicable = isClicable;
    }
    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        org.osmdroid.config.Configuration.getInstance().setUserAgentValue(requireContext().getPackageName());
        Log.d("MapFragment", "onCreate called, user agent set to: " + org.osmdroid.config.Configuration.getInstance().getUserAgentValue());
        // Use internal cache directory
        File basePath = requireContext().getCacheDir();
        org.osmdroid.config.Configuration.getInstance().setOsmdroidBasePath(basePath);
        org.osmdroid.config.Configuration.getInstance().setOsmdroidTileCache(
                new File(basePath, "osmdroid/tiles")
        );

        org.osmdroid.config.Configuration.getInstance().load(
                requireContext(),
                requireContext().getSharedPreferences("osmdroid", 0)
        );
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("MapFragment", "onCreateView called");
        View v = inflater.inflate(R.layout.fragment_map_driver, container, false);
        this.routeDrawer = new ViewModelProvider(requireActivity()).get(RouteDrawer.class);
        this.sheredLocationViewModel = new ViewModelProvider(requireActivity()).get(SheredLocationViewModel.class);
        this.markerDrawer = new ViewModelProvider(requireActivity()).get(MarkerDrawer.class);
        this.mapView = v.findViewById(R.id.map);
        this.mapView.setTileSource(TileSourceFactory.MAPNIK);
        this.mapView.setMultiTouchControls(isClicable);
        this.mapView.setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mapView.post(() -> {
            Log.d("MapFragment", "MapView size: " + mapView.getWidth() + "x" + mapView.getHeight());
            Log.d("MapFragment", "Setting map center and zoom");
            mapView.getController().setZoom(11.0);
            mapView.getController().setCenter(new GeoPoint(45.2671, 19.8335));
            mapView.invalidate();
        });
        this.mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                if(!isClicable) return false;
                Log.d("MapFragment", "Long press at: " + p.getLatitude() + ", " + p.getLongitude());
                sheredLocationViewModel.addLocation(p);
                return true;
            }
        }));
        sheredLocationViewModel.getStops().observe(getViewLifecycleOwner(), stops -> {
            mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker && "Stop".equals(((Marker) overlay).getSubDescription()));
            for (NominatimResult stop : stops) {
                Log.d("MapFragment", "Adding marker for stop: " + stop.display_name + " at (" + stop.getLat() + ", " + stop.getLon() + ")");
                GeoPoint point = new GeoPoint(stop.getLat(), stop.getLon());
                Marker marker = new Marker(mapView);
                marker.setPosition(point);
                marker.setSubDescription("Stop");
                marker.setTitle(stop.display_name);
                mapView.getOverlays().add(marker);
            }
            mapView.invalidate();
        });
        routeDrawer.getRoutes().observe(getViewLifecycleOwner(), routes -> {
            mapView.getOverlays().removeIf(overlay -> overlay instanceof Polyline);
            Log.d("MapFragment", "Updating routes, total routes: " + routes.size());
            for(var route : routes) {
                mapView.getOverlays().add(route.getRouteLine());
            }
                mapView.invalidate();
        });
        markerDrawer.getMarkers().observe(getViewLifecycleOwner(), markers -> {
            mapView.getOverlays().removeIf(overlay -> overlay instanceof Marker && "Marker".equals(((Marker) overlay).getSubDescription()));
            Log.d("MapFragment", "Updating markers, total markers: " + markers.size());
            for(var marker : markers) {
                Marker mapMarker = new Marker(mapView);
                mapMarker.setPosition(new GeoPoint(marker.getLatitude(), marker.getLongitude()));
                mapMarker.setTitle(marker.getName());
                mapMarker.setSubDescription("Marker");
                mapMarker.setIcon(marker.getIcon());
                mapView.getOverlays().add(mapMarker);

            }
            mapView.invalidate();
        });
        setupMapTouchHandling();
        return v;
    }
    @SuppressLint("ClickableViewAccessibility")
    private void setupMapTouchHandling() {
        mapView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int action = event.getAction();

                switch (action) {
                    case MotionEvent.ACTION_DOWN:
                        // Disable parent ScrollView scrolling when touching the map
                        v.getParent().requestDisallowInterceptTouchEvent(true);
                        break;

                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Re-enable parent ScrollView scrolling
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }

                // Let the map handle the touch event normally
                return false;
            }
        });
    }

        @Override
    public void onResume() {
        super.onResume();
        if (mapView != null) {
            mapView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mapView != null) {
            mapView.onPause();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mapView != null) {
            mapView.onDetach();
        }
    }
}