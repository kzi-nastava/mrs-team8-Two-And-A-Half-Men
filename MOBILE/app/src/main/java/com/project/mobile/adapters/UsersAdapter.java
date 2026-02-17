package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.users.User;
import com.project.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {

    private List<User> users;
    private OnUserClickListener listener;

    public interface OnUserClickListener {
        void onUserClick(User user);
    }

    public UsersAdapter() {
        this.users = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    public void setOnUserClickListener(OnUserClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvName, tvEmail, tvRole, tvDriverStatus, tvBlocked, tvPendingRequests;
        View roleBadge, statusBadge, blockedIndicator;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvUserId);
            tvName = itemView.findViewById(R.id.tvUserName);
            tvEmail = itemView.findViewById(R.id.tvUserEmail);
            tvRole = itemView.findViewById(R.id.tvUserRole);
            tvDriverStatus = itemView.findViewById(R.id.tvDriverStatus);
            tvBlocked = itemView.findViewById(R.id.tvBlocked);
            tvPendingRequests = itemView.findViewById(R.id.tvPendingRequests);
            roleBadge = itemView.findViewById(R.id.roleBadge);
            statusBadge = itemView.findViewById(R.id.statusBadge);
            blockedIndicator = itemView.findViewById(R.id.blockedIndicator);
        }

        void bind(User user, OnUserClickListener listener) {
            tvId.setText(String.valueOf(user.getId()));
            tvName.setText(user.getFirstName() + " " + user.getLastName());
            tvEmail.setText(user.getEmail());
            tvRole.setText(user.getRole());
            
            // Set role badge background
            setRoleBadgeColor(user.getRole());
            
            // Driver status
            if (user.getDriverStatus() != null) {
                tvDriverStatus.setText(user.getDriverStatus());
                tvDriverStatus.setVisibility(View.VISIBLE);
                statusBadge.setVisibility(View.VISIBLE);
                setStatusBadgeColor(user.getDriverStatus());
            } else {
                tvDriverStatus.setVisibility(View.GONE);
                statusBadge.setVisibility(View.GONE);
            }
            
            // Blocked status
            tvBlocked.setText(user.isBlocked() ? "🔒 Yes" : "✅ No");
            blockedIndicator.setBackgroundColor(
                itemView.getContext().getColor(
                    user.isBlocked() ? R.color.error_red : R.color.success_green
                )
            );
            
            // Pending requests
            if (user.getHasPendingRequests() != null) {
                tvPendingRequests.setText(user.getHasPendingRequests() ? "⏳ Yes" : "✅ No");
            } else {
                tvPendingRequests.setText("N/A");
            }
            
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onUserClick(user);
                }
            });
        }

        private void setRoleBadgeColor(String role) {
            int colorRes;
            switch (role) {
                case "ADMIN":
                    colorRes = R.color.badge_admin;
                    break;
                case "DRIVER":
                    colorRes = R.color.badge_driver;
                    break;
                case "CUSTOMER":
                    colorRes = R.color.badge_customer;
                    break;
                default:
                    colorRes = R.color.secondary_text;
                    break;
            }
            roleBadge.setBackgroundColor(itemView.getContext().getColor(colorRes));
        }

        private void setStatusBadgeColor(String status) {
            int colorRes;
            switch (status) {
                case "BUSY":
                    colorRes = R.color.badge_busy;
                    break;
                case "AVAILABLE":
                    colorRes = R.color.badge_available;
                    break;
                case "INACTIVE":
                    colorRes = R.color.badge_inactive;
                    break;
                default:
                    colorRes = R.color.secondary_text;
                    break;
            }
            statusBadge.setBackgroundColor(itemView.getContext().getColor(colorRes));
        }
    }
}
