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

import java.util.ArrayList;
import java.util.List;

public class AdditionalServicesAdapter extends RecyclerView.Adapter<AdditionalServicesAdapter.ViewHolder> {

    private List<AdditionalService> services;
    private List<String> selectedServiceNames;
    private boolean readonly = false;

    public AdditionalServicesAdapter() {
        this.services = new ArrayList<>();
        this.selectedServiceNames = new ArrayList<>();
    }

    public void setServices(List<AdditionalService> services) {
        this.services = services;
        notifyDataSetChanged();
    }

    public void setSelectedServices(List<String> selectedServiceNames) {
        this.selectedServiceNames = selectedServiceNames != null ? new ArrayList<>(selectedServiceNames) : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void setReadonly(boolean readonly) {
        this.readonly = readonly;
        notifyDataSetChanged();
    }

    public List<String> getSelectedServiceNames() {
        return new ArrayList<>(selectedServiceNames);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_additional_service, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AdditionalService service = services.get(position);
        holder.bind(service);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        CheckBox checkboxService;
        TextView tvServiceName;
        TextView tvServiceDescription;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            checkboxService = itemView.findViewById(R.id.checkboxService);
            tvServiceName = itemView.findViewById(R.id.tvServiceName);
            tvServiceDescription = itemView.findViewById(R.id.tvServiceDescription);
        }

        void bind(AdditionalService service) {
            tvServiceName.setText(service.getName());
            tvServiceDescription.setText(service.getDescription());
            
            boolean isChecked = selectedServiceNames.contains(service.getName());
            checkboxService.setChecked(isChecked);
            checkboxService.setEnabled(!readonly);

            itemView.setOnClickListener(v -> {
                if (!readonly) {
                    toggleService(service.getName());
                }
            });

            checkboxService.setOnCheckedChangeListener((buttonView, isChecked1) -> {
                if (!readonly && buttonView.isPressed()) {
                    toggleService(service.getName());
                }
            });
        }
    }

    private void toggleService(String serviceName) {
        if (selectedServiceNames.contains(serviceName)) {
            selectedServiceNames.remove(serviceName);
        } else {
            selectedServiceNames.add(serviceName);
        }
        notifyDataSetChanged();
    }
}
