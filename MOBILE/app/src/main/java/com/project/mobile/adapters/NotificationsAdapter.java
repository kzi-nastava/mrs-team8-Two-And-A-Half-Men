package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.notifications.Notification;
import com.project.mobile.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private List<Notification> notifications = new ArrayList<>();
    private OnNotificationClickListener clickListener;
    private OnNotificationDeleteListener deleteListener;

    public interface OnNotificationClickListener {
        void onNotificationClick(Notification notification);
    }

    public interface OnNotificationDeleteListener {
        void onNotificationDelete(Notification notification);
    }

    public void setNotifications(List<Notification> notifications) {
        this.notifications = notifications;
        notifyDataSetChanged();
    }

    public void setOnNotificationClickListener(OnNotificationClickListener listener) {
        this.clickListener = listener;
    }

    public void setOnNotificationDeleteListener(OnNotificationDeleteListener listener) {
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
            .inflate(R.layout.item_notification, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Notification notification = notifications.get(position);
        holder.bind(notification, clickListener, deleteListener);
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View cardView, unreadIndicator;
        TextView tvIcon, tvTitle, tvMessage, tvTime, tvActionLink;
        ImageButton btnDelete;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView;
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
            tvIcon = itemView.findViewById(R.id.tvNotificationIcon);
            tvTitle = itemView.findViewById(R.id.tvNotificationTitle);
            tvMessage = itemView.findViewById(R.id.tvNotificationMessage);
            tvTime = itemView.findViewById(R.id.tvNotificationTime);
            tvActionLink = itemView.findViewById(R.id.tvActionLink);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        void bind(Notification notification, OnNotificationClickListener clickListener,
                  OnNotificationDeleteListener deleteListener) {
            
            tvTitle.setText(notification.getTitle());
            tvMessage.setText(notification.getMessage());
            tvTime.setText(formatTimestamp(notification.getTimestamp()));
            tvIcon.setText(getNotificationIcon(notification.getTitle()));

            // Unread styling
            if (!notification.isRead()) {
                cardView.setSelected(true);
                unreadIndicator.setVisibility(View.VISIBLE);
            } else {
                cardView.setSelected(false);
                unreadIndicator.setVisibility(View.GONE);
            }

            // Action link
            if (notification.getAdditionalData() != null && !notification.getAdditionalData().isEmpty()) {
                tvActionLink.setVisibility(View.VISIBLE);
                cardView.setClickable(true);
            } else {
                tvActionLink.setVisibility(View.GONE);
                cardView.setClickable(false);
            }

            // Click listeners
            cardView.setOnClickListener(v -> {
                if (clickListener != null) {
                    clickListener.onNotificationClick(notification);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (deleteListener != null) {
                    deleteListener.onNotificationDelete(notification);
                }
            });
        }

        private String formatTimestamp(String timestamp) {
            try {
                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
                Date date = inputFormat.parse(timestamp);
                if (date == null) return "Unknown";

                long diff = System.currentTimeMillis() - date.getTime();
                long seconds = diff / 1000;
                long minutes = seconds / 60;
                long hours = minutes / 60;
                long days = hours / 24;

                if (seconds < 60) {
                    return "Just now";
                } else if (minutes < 60) {
                    return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
                } else if (hours < 24) {
                    return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
                } else if (days < 7) {
                    return days + " day" + (days > 1 ? "s" : "") + " ago";
                } else {
                    SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.getDefault());
                    return outputFormat.format(date);
                }
            } catch (Exception e) {
                return timestamp;
            }
        }

        private String getNotificationIcon(String title) {
            String lowerTitle = title.toLowerCase();
            if (lowerTitle.contains("ride")) return "🚗";
            if (lowerTitle.contains("payment")) return "💳";
            if (lowerTitle.contains("driver")) return "👨‍✈️";
            if (lowerTitle.contains("cancel")) return "❌";
            if (lowerTitle.contains("complete")) return "✅";
            if (lowerTitle.contains("panic")) return "🚨";
            if (lowerTitle.contains("message")) return "💬";
            return "🔔";
        }
    }
}
