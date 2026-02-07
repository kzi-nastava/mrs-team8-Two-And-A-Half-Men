package com.project.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.project.mobile.R;
import com.project.mobile.models.Ride;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideViewHolder> {

    private List<Ride> rideList;

    public RideHistoryAdapter(List<Ride> rideList) {
        this.rideList = rideList;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_rides_history, parent, false);
        return new RideViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rideList.get(position);

        holder.userEmail.setText("User: " + ride.getRideOwnerName());
        holder.scheduled.setText("Scheduled:\n" + ride.getScheduledTime());
        holder.started.setText("Started:\n" + ride.getStartTime());
        holder.ended.setText("Ended:\n" + ride.getEndTime());
        holder.status.setText("Status: " + ride.getStatus());
        holder.price.setText("Price: " + ride.getPrice());
        holder.passengers.setText("Passengers: " + ride.getPassengersMails());
    }

    @Override
    public int getItemCount() {
        return rideList.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        TextView userEmail, scheduled, started, ended, status, price, passengers;

        public RideViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmail = itemView.findViewById(R.id.userEmail);
            scheduled = itemView.findViewById(R.id.scheduled);
            started = itemView.findViewById(R.id.started);
            ended = itemView.findViewById(R.id.ended);
            status = itemView.findViewById(R.id.status);
            price = itemView.findViewById(R.id.price);
            passengers = itemView.findViewById(R.id.passengers);
        }
    }
}