package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.Ride.RouteItemDTO;
import com.project.mobile.DTO.routes.FavouriteRoute;
import com.project.mobile.R;

import java.util.List;

public class FavouriteRoutesAdapter extends RecyclerView.Adapter<FavouriteRoutesAdapter.ViewHolder> {

    private final List<FavouriteRoute> routes;
    private final OnSelectListener onSelectListener;

    public interface OnSelectListener {
        void onSelect(FavouriteRoute route);
    }

    public FavouriteRoutesAdapter(List<FavouriteRoute> routes, OnSelectListener listener) {
        this.routes = routes;
        this.onSelectListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_favourite_route, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavouriteRoute route = routes.get(position);
        List<RouteItemDTO> points = route.getPoints();
        
        if (points != null && !points.isEmpty()) {
            // Start address
            holder.tvStartAddress.setText(points.get(0).getAddress());
            
            // End address
            holder.tvEndAddress.setText(points.get(points.size() - 1).getAddress());
            
            // Number of stops
            int stops = points.size() - 2; // Exclude start and end
            if (stops > 0) {
                holder.tvStops.setVisibility(View.VISIBLE);
                holder.tvStops.setText(stops + " stop" + (stops > 1 ? "s" : ""));
            } else {
                holder.tvStops.setVisibility(View.GONE);
            }
        }
        
        holder.btnSelect.setOnClickListener(v -> {
            if (onSelectListener != null) {
                onSelectListener.onSelect(route);
            }
        });
    }

    @Override
    public int getItemCount() {
        return routes.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvStartAddress;
        TextView tvEndAddress;
        TextView tvStops;
        Button btnSelect;

        ViewHolder(View itemView) {
            super(itemView);
            tvStartAddress = itemView.findViewById(R.id.tv_start_address);
            tvEndAddress = itemView.findViewById(R.id.tv_end_address);
            tvStops = itemView.findViewById(R.id.tv_stops);
            btnSelect = itemView.findViewById(R.id.btn_select_route);
        }
    }
}
