package com.project.mobile.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.project.mobile.R;
import com.project.mobile.map.MapFragment;
import com.project.mobile.map.mapForm.FormStops;
import com.project.mobile.map.mapForm.SearchFragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HomeUnregistered#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HomeUnregistered extends Fragment {


    public HomeUnregistered() {
    }

    public static HomeUnregistered newInstance() {
        HomeUnregistered fragment = new HomeUnregistered();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home_unregistered, container, false);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.post(() -> {
            Log.d("HomeUnregistered", "Fragment view size: " + view.getWidth() + "x" + view.getHeight());
            FrameLayout mapContainer = view.findViewById(R.id.map_container);
            Log.d("HomeUnregistered", "Map container size: " + mapContainer.getWidth() + "x" + mapContainer.getHeight());
        });
        getChildFragmentManager().beginTransaction()
                .replace(R.id.search_container , new SearchFragment())
                .replace(R.id.map_container, new MapFragment())
                .replace(R.id.info_container , new FormStops(2L))
                .commit();
    }
}