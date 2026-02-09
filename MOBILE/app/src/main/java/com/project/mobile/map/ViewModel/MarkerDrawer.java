package com.project.mobile.map.ViewModel;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.project.mobile.DTO.Map.MarkerPointIcon;

import java.util.ArrayList;
import java.util.List;

public class MarkerDrawer extends ViewModel {
    private final MutableLiveData<List<MarkerPointIcon>> markers =
            new MutableLiveData<>(new ArrayList<>());

    public MutableLiveData<List<MarkerPointIcon>> getMarkers() {
        return markers;
    }

    public void addMarker(MarkerPointIcon markerPointIcon) {
            List<MarkerPointIcon> currentMarkerPointIcons = markers.getValue();
            for (MarkerPointIcon m : currentMarkerPointIcons) {
                Log.d("MarkerDrawer", "Comparing marker: " + m.getName() + " with new marker: " + markerPointIcon.getName());
                if (m.getName().equals(markerPointIcon.getName())) {
                    Log.d("MarkerDrawer", "Marker with name " + markerPointIcon.getName() + " already exists. Removing it.");
                    currentMarkerPointIcons.remove(m);
                }
            }
            currentMarkerPointIcons.add(markerPointIcon);
            markers.postValue(currentMarkerPointIcons);
    }

    public void clearMarkers() {
        markers.postValue(new ArrayList<>());
    }
}
