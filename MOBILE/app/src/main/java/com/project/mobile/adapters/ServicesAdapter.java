package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.vehicles.AdditionalService;
import com.project.mobile.R;

import java.util.List;
import java.util.Set;

public class ServicesAdapter extends RecyclerView.Adapter<ServicesAdapter.ViewHolder> {

    private final List<AdditionalService> services;
    private final Set<Long> selectedServiceIds;
    private final OnToggleListener onToggleListener;

    public interface OnToggleListener {
        void onToggle(long serviceId);
    }

    public ServicesAdapter(List<AdditionalService> services, 
                          Set<Long> selectedServiceIds,
                          OnToggleListener listener) {
        this.services = services;
        this.selectedServiceIds = selectedServiceIds;
        this.onToggleListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdditionalService service = services.get(position);
        holder.tvServiceName.setText(service.getName());
        holder.checkBox.setChecked(selectedServiceIds.contains(service.getId()));
        
        holder.itemView.setOnClickListener(v -> {
            if (onToggleListener != null) {
                onToggleListener.onToggle(service.getId());
            }
        });
        
        holder.checkBox.setOnClickListener(v -> {
            if (onToggleListener != null) {
                onToggleListener.onToggle(service.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkBox;
        TextView tvServiceName;

        ViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.cb_service);
            tvServiceName = itemView.findViewById(R.id.tv_service_name);
        }
    }
}
