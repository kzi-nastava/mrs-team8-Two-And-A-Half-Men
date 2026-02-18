package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.reports.AggregatedUserReportDTO;
import com.project.mobile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserReportCardAdapter extends RecyclerView.Adapter<UserReportCardAdapter.ViewHolder> {

    private List<AggregatedUserReportDTO> userReports = new ArrayList<>();
    private OnUserClickListener listener;
    private Long selectedUserId = null;

    public interface OnUserClickListener {
        void onUserClick(long userId);
    }

    public UserReportCardAdapter() {}

    public void setUserReports(List<AggregatedUserReportDTO> reports) {
        this.userReports = reports;
        notifyDataSetChanged();
    }

    public void setSelectedUserId(Long userId) {
        this.selectedUserId = userId;
        notifyDataSetChanged();
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_user_report_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AggregatedUserReportDTO userReport = userReports.get(position);
        holder.bind(userReport, selectedUserId, listener);
    }

    @Override
    public int getItemCount() {
        return userReports.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserEmail;
        TextView tvRides, tvDistance, tvAmount;
        View cardView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView;
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRides = itemView.findViewById(R.id.tvRides);
            tvDistance = itemView.findViewById(R.id.tvDistance);
            tvAmount = itemView.findViewById(R.id.tvAmount);
        }

        void bind(AggregatedUserReportDTO userReport, Long selectedUserId, OnUserClickListener listener) {
            tvUserName.setText(userReport.getUserName());
            tvUserEmail.setText(userReport.getUserEmail());
            tvRides.setText(String.valueOf(userReport.getReport().getTotalRides()));
            tvDistance.setText(String.format(Locale.getDefault(), "%.2f km", 
                userReport.getReport().getTotalDistance()));
            tvAmount.setText(String.format(Locale.getDefault(), "%.2f RSD", 
                userReport.getReport().getTotalAmount()));

            // Highlight if selected
            boolean isSelected = selectedUserId != null && selectedUserId == userReport.getUserId();
            cardView.setSelected(isSelected);

            cardView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(userReport.getUserId());
                }
            });
        }
    }
}
