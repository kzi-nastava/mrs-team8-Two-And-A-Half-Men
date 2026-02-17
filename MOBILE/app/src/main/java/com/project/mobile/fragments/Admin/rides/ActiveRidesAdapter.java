package com.project.mobile.fragments.Admin.rides;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.R;
import com.project.mobile.databinding.ItemActiveRideBinding;
import com.project.mobile.models.Ride;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ActiveRidesAdapter extends RecyclerView.Adapter<ActiveRidesAdapter.ActiveRideViewHolder> {

    private final List<Ride> rides;
    private final OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
    }

    public ActiveRidesAdapter(List<Ride> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ActiveRideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemActiveRideBinding binding = ItemActiveRideBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new ActiveRideViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ActiveRideViewHolder holder, int position) {
        holder.bind(rides.get(position), listener);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class ActiveRideViewHolder extends RecyclerView.ViewHolder {
        private final ItemActiveRideBinding binding;

        public ActiveRideViewHolder(@NonNull ItemActiveRideBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Ride ride, OnRideClickListener listener) {
            binding.statusBadge.setText(ride.getStatus());
            setStatusColor(ride.getStatus());

            binding.panicIndicator.setVisibility(
                    "PANICKED".equals(ride.getStatus()) ? View.VISIBLE : View.GONE
            );

            if (ride.getAddresses() != null && !ride.getAddresses().isEmpty()) {
                binding.startPoint.setText(ride.getAddresses().get(0).getAddress());
                binding.destination.setText(
                        ride.getAddresses().get(ride.getAddresses().size() - 1).getAddress()
                );
            }

            if (ride.getStartTime() != null && !ride.getStartTime().isEmpty()) {
                binding.startTime.setText(formatDateTime(ride.getStartTime()));
                binding.startTimeContainer.setVisibility(View.VISIBLE);
            } else {
                binding.startTimeContainer.setVisibility(View.GONE);
            }

            String driverName = ride.getDriverName();
            if (driverName != null && !driverName.isEmpty()) {
                binding.driverName.setText(driverName);
                binding.driverContainer.setVisibility(View.VISIBLE);
            } else {
                binding.driverContainer.setVisibility(View.GONE);
            }

            binding.rideOwner.setText(ride.getRideOwnerName());

            if (ride.getPassengersMails() != null) {
                int count = ride.getPassengersMails().size();
                binding.passengers.setText(count + " passenger" + (count != 1 ? "s" : ""));
            }

            binding.getRoot().setOnClickListener(v -> listener.onRideClick(ride));
        }

        private void setStatusColor(String status) {
            int colorResId;
            switch (status) {
                case "PENDING":    colorResId = R.color.status_pending;    break;
                case "ACCEPTED":   colorResId = R.color.status_accepted;   break;
                case "ACTIVE":     colorResId = R.color.status_active;     break;
                case "FINISHED":   colorResId = R.color.status_finished;   break;
                case "INTERRUPTED":colorResId = R.color.status_interrupted;break;
                case "CANCELLED":  colorResId = R.color.status_cancelled;  break;
                case "PANICKED":   colorResId = R.color.status_panicked;   break;
                default:           colorResId = R.color.status_default;
            }
            int color = ContextCompat.getColor(binding.getRoot().getContext(), colorResId);
            binding.statusBadge.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(color)
            );
        }

        private String formatDateTime(String isoDateTime) {
            if (isoDateTime == null || isoDateTime.isEmpty()) return "N/A";
            try {
                LocalDateTime dt = LocalDateTime.parse(isoDateTime, DateTimeFormatter.ISO_DATE_TIME);
                return dt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm", Locale.getDefault()));
            } catch (Exception e) {
                return isoDateTime;
            }
        }
    }
}