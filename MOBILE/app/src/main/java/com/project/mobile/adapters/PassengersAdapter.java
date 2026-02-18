package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.R;

import java.util.List;

public class PassengersAdapter extends RecyclerView.Adapter<PassengersAdapter.ViewHolder> {

    private final List<String> passengers;
    private final OnRemoveClickListener onRemoveClickListener;

    public interface OnRemoveClickListener {
        void onRemove(int position);
    }

    public PassengersAdapter(List<String> passengers, OnRemoveClickListener listener) {
        this.passengers = passengers;
        this.onRemoveClickListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_passenger, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String email = passengers.get(position);
        holder.tvEmail.setText(email);
        holder.btnRemove.setOnClickListener(v -> {
            if (onRemoveClickListener != null) {
                onRemoveClickListener.onRemove(holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return passengers.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvEmail;
        ImageButton btnRemove;

        ViewHolder(View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.tv_passenger_email);
            btnRemove = itemView.findViewById(R.id.btn_remove_passenger);
        }
    }
}
