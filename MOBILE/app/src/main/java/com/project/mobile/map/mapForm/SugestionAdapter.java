package com.project.mobile.map.mapForm;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.DTO.NominatimResult;
import com.project.mobile.R;

import java.util.ArrayList;
import java.util.List;

public class SugestionAdapter extends RecyclerView.Adapter<SugestionAdapter.ViewHolder> {
    public interface OnSuggestionClickListener {
        void onSuggestionClick(NominatimResult result);
    }

    private List<NominatimResult> suggestions = new ArrayList<>();
    private final OnSuggestionClickListener listener;

    public SugestionAdapter(OnSuggestionClickListener listener) {
        this.listener = listener;
    }

    /**
     * Updates the suggestions list and refreshes RecyclerView
     */
    public void submitList(List<NominatimResult> newSuggestions) {
        this.suggestions = newSuggestions != null ? newSuggestions : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_sugestion, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NominatimResult result = suggestions.get(position);
        holder.txtName.setText(result.display_name);
        holder.itemView.setOnClickListener(v -> listener.onSuggestionClick(result));
    }


    @Override
    public int getItemCount() {
        return suggestions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtName;

        ViewHolder(View view) {
            super(view);
            txtName = view.findViewById(R.id.txt_suggestion_name);
        }
    }
}
