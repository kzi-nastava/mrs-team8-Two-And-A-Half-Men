package com.project.mobile.fragments.Admin.settings;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.vehicles.VehicleType;
import com.project.mobile.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class VehiclePricingAdapter extends RecyclerView.Adapter<VehiclePricingAdapter.ViewHolder> {

    public interface PricingListener {
        void onSavePrice(VehicleType vehicleType, double newPrice);
    }

    private List<VehicleType> vehicleTypes = new ArrayList<>();
    private long editingId = -1;
    private long savingId = -1;
    private final PricingListener listener;

    public VehiclePricingAdapter(PricingListener listener) {
        this.listener = listener;
    }

    public void setVehicleTypes(List<VehicleType> types) {
        this.vehicleTypes = new ArrayList<>(types);
        notifyDataSetChanged();
    }

    public void setEditingId(long id) {
        long oldEditing = this.editingId;
        this.editingId = id;
        if (oldEditing != -1) notifyItemChanged(indexOfId(oldEditing));
        if (id != -1) notifyItemChanged(indexOfId(id));
    }

    public void setSavingId(long id) {
        this.savingId = id;
        notifyItemChanged(indexOfId(id));
    }

    public void updateItem(VehicleType updated) {
        int idx = indexOfId(updated.getId());
        if (idx != -1) {
            vehicleTypes.set(idx, updated);
            notifyItemChanged(idx);
        }
    }

    private int indexOfId(long id) {
        for (int i = 0; i < vehicleTypes.size(); i++) {
            if (vehicleTypes.get(i).getId() == id) return i;
        }
        return -1;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_vehicle_pricing, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VehicleType item = vehicleTypes.get(position);
        boolean isEditing = editingId == item.getId();
        boolean isSaving = savingId == item.getId();
        boolean anyEditing = editingId != -1;

        holder.tvVehicleIcon.setText(getVehicleIcon(item.getTypeName()));
        holder.tvVehicleName.setText(item.getTypeName());
        holder.tvVehicleDescription.setText(item.getDescription());

        if (isEditing) {
            holder.viewModeLayout.setVisibility(View.GONE);
            holder.editModeLayout.setVisibility(View.VISIBLE);
            holder.itemView.setBackground(
                    holder.itemView.getContext().getDrawable(R.drawable.bg_card_editing));

            // Pre-fill current price
            holder.etPrice.setText(String.format(Locale.US, "%.2f", item.getPrice()));
            holder.etPrice.selectAll();

            holder.btnSave.setEnabled(!isSaving);
            holder.btnCancel.setEnabled(!isSaving);
            holder.btnSave.setText(isSaving ? "Saving..." : "✓ Save");

            holder.btnSave.setOnClickListener(v -> {
                String input = holder.etPrice.getText().toString().trim();
                if (!input.isEmpty()) {
                    try {
                        double newPrice = Double.parseDouble(input);
                        listener.onSavePrice(item, newPrice);
                    } catch (NumberFormatException ignored) {}
                }
            });

            holder.btnCancel.setOnClickListener(v -> {
                setEditingId(-1);
            });

        } else {
            holder.viewModeLayout.setVisibility(View.VISIBLE);
            holder.editModeLayout.setVisibility(View.GONE);
            holder.itemView.setBackground(
                    holder.itemView.getContext().getDrawable(R.drawable.bg_card_default));

            holder.tvPrice.setText(String.format(Locale.US, "%.2f", item.getPrice()));
            holder.btnEditPrice.setEnabled(!anyEditing);
            holder.btnEditPrice.setAlpha(anyEditing ? 0.5f : 1.0f);

            holder.btnEditPrice.setOnClickListener(v -> {
                setEditingId(item.getId());
            });
        }
    }

    @Override
    public int getItemCount() {
        return vehicleTypes.size();
    }

    private String getVehicleIcon(String typeName) {
        if (typeName == null) return "🚗";
        String lower = typeName.toLowerCase();
        if (lower.contains("standard")) return "🚗";
        if (lower.contains("comfort")) return "🚙";
        if (lower.contains("luxury") || lower.contains("premium")) return "🚘";
        if (lower.contains("van")) return "🚐";
        if (lower.contains("electric")) return "⚡";
        return "🚗";
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvVehicleIcon, tvVehicleName, tvVehicleDescription, tvPrice;
        Button btnEditPrice, btnSave, btnCancel;
        EditText etPrice;
        View viewModeLayout, editModeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVehicleIcon = itemView.findViewById(R.id.tv_vehicle_icon);
            tvVehicleName = itemView.findViewById(R.id.tv_vehicle_name);
            tvVehicleDescription = itemView.findViewById(R.id.tv_vehicle_description);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnEditPrice = itemView.findViewById(R.id.btn_edit_price);
            btnSave = itemView.findViewById(R.id.btn_save);
            btnCancel = itemView.findViewById(R.id.btn_cancel);
            etPrice = itemView.findViewById(R.id.et_price);
            viewModeLayout = itemView.findViewById(R.id.layout_view_mode);
            editModeLayout = itemView.findViewById(R.id.layout_edit_mode);
        }
    }
}