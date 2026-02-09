package com.project.mobile;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.databinding.ItemRideHistoryBinding;
import com.project.mobile.models.Ride;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class RideHistoryAdapter extends RecyclerView.Adapter<RideHistoryAdapter.RideViewHolder> {

    private final List<Ride> rides;
    private final OnRideClickListener listener;

    public interface OnRideClickListener {
        void onRideClick(Ride ride);
    }

    public RideHistoryAdapter(List<Ride> rides, OnRideClickListener listener) {
        this.rides = rides;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RideViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRideHistoryBinding binding = ItemRideHistoryBinding.inflate(
                LayoutInflater.from(parent.getContext()),
                parent,
                false
        );
        return new RideViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RideViewHolder holder, int position) {
        Ride ride = rides.get(position);
        holder.bind(ride, listener);
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    static class RideViewHolder extends RecyclerView.ViewHolder {
        private final ItemRideHistoryBinding binding;

        public RideViewHolder(@NonNull ItemRideHistoryBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Ride ride, OnRideClickListener listener) {
            // Status
            binding.statusBadge.setText(ride.getStatus());
            setStatusColor(ride.getStatus());

            // Panic indicator
            if ("PANICKED".equals(ride.getStatus())) {
                binding.panicIndicator.setVisibility(View.VISIBLE);
            } else {
                binding.panicIndicator.setVisibility(View.GONE);
            }

            // Addresses
            if (ride.getAddresses() != null && !ride.getAddresses().isEmpty()) {
                binding.startPoint.setText(ride.getAddresses().get(0));
                binding.destination.setText(ride.getAddresses().get(ride.getAddresses().size() - 1));
            }

            // Times
            if (ride.getStartTime() != null && !ride.getStartTime().isEmpty()) {
                binding.startTime.setText(formatDateTime(ride.getStartTime()));
                binding.startTimeContainer.setVisibility(View.VISIBLE);
            } else {
                binding.startTimeContainer.setVisibility(View.GONE);
            }

            if (ride.getEndTime() != null && !ride.getEndTime().isEmpty()) {
                binding.endTime.setText(formatDateTime(ride.getEndTime()));
                binding.endTimeContainer.setVisibility(View.VISIBLE);
            } else {
                binding.endTimeContainer.setVisibility(View.GONE);
            }

            // Cost
            binding.totalCost.setText(String.format(Locale.getDefault(), "%.2f RSD", ride.getTotalCost()));

            // People (for driver view)
            binding.rideOwner.setText(ride.getRideOwnerName());
            if (ride.getPassengersMails() != null) {
                int passengerCount = ride.getPassengersMails().size();
                binding.passengers.setText(passengerCount + " passenger" + (passengerCount != 1 ? "s" : ""));
            }

            // Cancellation reason
            if ("CANCELLED".equals(ride.getStatus()) &&
                    ride.getCancellationReason() != null &&
                    !ride.getCancellationReason().isEmpty()) {
                binding.cancellationCard.setVisibility(View.VISIBLE);
                binding.cancellationReason.setText(ride.getCancellationReason());
            } else {
                binding.cancellationCard.setVisibility(View.GONE);
            }

            // Click listener
            binding.getRoot().setOnClickListener(v -> listener.onRideClick(ride));
        }

        private void setStatusColor(String status) {
            int colorResId;
            switch (status) {
                case "PENDING":
                    colorResId = R.color.status_pending;
                    break;
                case "ACCEPTED":
                    colorResId = R.color.status_accepted;
                    break;
                case "ACTIVE":
                    colorResId = R.color.status_active;
                    break;
                case "FINISHED":
                    colorResId = R.color.status_finished;
                    break;
                case "INTERRUPTED":
                    colorResId = R.color.status_interrupted;
                    break;
                case "CANCELLED":
                    colorResId = R.color.status_cancelled;
                    break;
                case "PANICKED":
                    colorResId = R.color.status_panicked;
                    break;
                default:
                    colorResId = R.color.status_default;
            }

            int color = ContextCompat.getColor(binding.getRoot().getContext(), colorResId);
            binding.statusBadge.setBackgroundTintList(
                    android.content.res.ColorStateList.valueOf(color)
            );
        }

        private String formatDateTime(String isoDateTime) {
            if (isoDateTime == null || isoDateTime.isEmpty()) {
                return "N/A";
            }
            try {
                DateTimeFormatter inputFormatter = DateTimeFormatter.ISO_DATE_TIME;
                LocalDateTime dateTime = LocalDateTime.parse(isoDateTime, inputFormatter);
                DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern(
                        "dd.MM.yyyy HH:mm",
                        Locale.getDefault()
                );
                return dateTime.format(outputFormatter);
            } catch (Exception e) {
                return isoDateTime;
            }
        }
    }
}