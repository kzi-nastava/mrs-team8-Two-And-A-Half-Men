package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.vehicles.VehicleType;
import com.project.mobile.R;

import java.util.List;

public class VehicleTypesAdapter extends RecyclerView.Adapter<VehicleTypesAdapter.ViewHolder> {

    private final List<VehicleType> vehicleTypes;
    private Long selectedVehicleTypeId;
    private final OnSelectListener onSelectListener;

    public interface OnSelectListener {
        void onSelect(long vehicleTypeId);
    }

    public VehicleTypesAdapter(List<VehicleType> vehicleTypes,
                              Long selectedVehicleTypeId,
                              OnSelectListener listener) {
        this.vehicleTypes = vehicleTypes;
        this.selectedVehicleTypeId = selectedVehicleTypeId;
        this.onSelectListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VehicleType vehicleType = vehicleTypes.get(position);
        
        holder.tvVehicleName.setText(vehicleType.getTypeName());
        holder.tvVehiclePrice.setText(String.format("%.2f per km", vehicleType.getPrice()));
        holder.radioButton.setChecked(selectedVehicleTypeId != null && 
                                     selectedVehicleTypeId == vehicleType.getId());
        
        holder.itemView.setOnClickListener(v -> {
            selectedVehicleTypeId = vehicleType.getId();
            notifyDataSetChanged();
            if (onSelectListener != null) {
                onSelectListener.onSelect(vehicleType.getId());
            }
        });
        
        holder.radioButton.setOnClickListener(v -> {
            selectedVehicleTypeId = vehicleType.getId();
            notifyDataSetChanged();
            if (onSelectListener != null) {
                onSelectListener.onSelect(vehicleType.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return vehicleTypes.size();
    }

    public void updateSelection(Long newSelectedId) {
        this.selectedVehicleTypeId = newSelectedId;
        notifyDataSetChanged();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        RadioButton radioButton;
        TextView tvVehicleName;
        TextView tvVehiclePrice;

        ViewHolder(View itemView) {
            super(itemView);
            radioButton = itemView.findViewById(R.id.rb_vehicle_type);
            tvVehicleName = itemView.findViewById(R.id.tv_vehicle_name);
            tvVehiclePrice = itemView.findViewById(R.id.tv_vehicle_price);
        }
    }
}
