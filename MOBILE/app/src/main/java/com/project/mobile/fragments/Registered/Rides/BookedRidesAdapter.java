package com.project.mobile.fragments.Registered.Rides;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.project.mobile.R;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.Ride.RideBookedDTO;


import java.util.ArrayList;
import java.util.List;

public class BookedRidesAdapter extends RecyclerView.Adapter<BookedRidesAdapter.RideViewHolder> {

private List<RideBookedDTO> rides = new ArrayList<>();
private OnRideClickListener clickListener;

public interface OnRideClickListener {
    void onRideClick(RideBookedDTO ride);
}

public BookedRidesAdapter(OnRideClickListener clickListener) {
    this.clickListener = clickListener;
}

public void setRides(List<RideBookedDTO> rides) {
    this.rides = rides;
    notifyDataSetChanged();
}

@NonNull
@Override
public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_booked_ride, parent, false);
    return new RideViewHolder(view);
}

@Override
public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
    RideBookedDTO ride = rides.get(position);
    holder.bind(ride, clickListener);
}

@Override
public int getItemCount() {
    return rides.size();
}

static class RideViewHolder extends RecyclerView.ViewHolder {
    private CardView cardView;
    private TextView txtRoute;
    private TextView txtDriver;
    private TextView txtStatus;
    private TextView txtScheduled;

    public RideViewHolder(@NonNull View itemView) {
        super(itemView);
        cardView = (CardView) itemView;
        txtRoute = itemView.findViewById(R.id.txt_route);
        txtDriver = itemView.findViewById(R.id.txt_driver);
        txtStatus = itemView.findViewById(R.id.txt_status);
        txtScheduled = itemView.findViewById(R.id.txt_scheduled);
    }

    public void bind(RideBookedDTO ride, OnRideClickListener clickListener) {
        txtRoute.setText(ride.getRoute());
        txtDriver.setText("Driver: " + ride.getDriverName());
        txtStatus.setText("Status: " + ride.getStatus());

        // Show schedule time or start time
        if (ride.getScheduleTime() != null && !ride.getScheduleTime().isEmpty()) {
            txtScheduled.setText("Scheduled: " + ride.getScheduleTime());
        } else if (ride.getStartTime() != null && !ride.getStartTime().isEmpty()) {
            txtScheduled.setText("Started: " + ride.getStartTime());
        } else {
            txtScheduled.setText("Scheduled: Immediate");
        }

        // Set click listener on the whole card
        cardView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onRideClick(ride);
            }
        });

        // Optional: Change card appearance based on status
        setStatusColor(ride.getStatus());
    }

    private void setStatusColor(String status) {
        int color;
        if ("ACTIVE".equalsIgnoreCase(status)) {
            color = 0xFFFFFFF; // White
        } else if ("PENDING".equalsIgnoreCase(status)) {
            color = 0xFFFFC107; // Amber/Yellow
        } else if ("COMPLETED".equalsIgnoreCase(status)) {
            color = 0xFF9E9E9E; // Gray
        } else {
            color = 0xFF2196F3; // Blue (default)
        }
        // You can use this color to set background or border
        // cardView.setCardBackgroundColor(color);
    }
}
}
