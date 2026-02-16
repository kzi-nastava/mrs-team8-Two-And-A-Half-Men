package com.project.mobile.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.project.mobile.R;
import com.project.mobile.helpers.ImageUrlHelper;

import java.util.ArrayList;
import java.util.List;

public class PendingChangesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_TEXT_CHANGE = 0;
    private static final int TYPE_IMAGE_CHANGE = 1;

    private List<ChangeItem> changes;

    public static class ChangeItem {
        public String field;
        public String oldValue;
        public String newValue;
        public boolean isImage;

        public ChangeItem(String field, String oldValue, String newValue, boolean isImage) {
            this.field = field;
            this.oldValue = oldValue;
            this.newValue = newValue;
            this.isImage = isImage;
        }
    }

    public PendingChangesAdapter() {
        this.changes = new ArrayList<>();
    }

    public void setChanges(List<ChangeItem> changes) {
        this.changes = changes;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return changes.get(position).isImage ? TYPE_IMAGE_CHANGE : TYPE_TEXT_CHANGE;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_IMAGE_CHANGE) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pending_image_change, parent, false);
            return new ImageChangeViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_pending_change, parent, false);
            return new TextChangeViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChangeItem change = changes.get(position);
        if (holder instanceof ImageChangeViewHolder) {
            ((ImageChangeViewHolder) holder).bind(change);
        } else {
            ((TextChangeViewHolder) holder).bind(change);
        }
    }

    @Override
    public int getItemCount() {
        return changes.size();
    }

    static class TextChangeViewHolder extends RecyclerView.ViewHolder {
        TextView tvChangeField;
        TextView tvOldValue;
        TextView tvNewValue;

        TextChangeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChangeField = itemView.findViewById(R.id.tvChangeField);
            tvOldValue = itemView.findViewById(R.id.tvOldValue);
            tvNewValue = itemView.findViewById(R.id.tvNewValue);
        }

        void bind(ChangeItem change) {
            tvChangeField.setText(change.field);
            tvOldValue.setText(change.oldValue != null ? change.oldValue : "—");
            tvNewValue.setText(change.newValue != null ? change.newValue : "—");
        }
    }

    static class ImageChangeViewHolder extends RecyclerView.ViewHolder {
        ImageView ivOldImage;
        ImageView ivNewImage;

        ImageChangeViewHolder(@NonNull View itemView) {
            super(itemView);
            ivOldImage = itemView.findViewById(R.id.ivOldImage);
            ivNewImage = itemView.findViewById(R.id.ivNewImage);
        }

        void bind(ChangeItem change) {
            if (change.oldValue != null && !change.oldValue.isEmpty()) {
                String fullUrl = ImageUrlHelper.getFullImageUrl(change.oldValue);
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.default_avatar)
                        .into(ivOldImage);
            } else {
                ivOldImage.setImageResource(R.drawable.default_avatar);
            }

            if (change.newValue != null && !change.newValue.isEmpty()) {
                String fullUrl = ImageUrlHelper.getFullImageUrl(change.newValue);
                Glide.with(itemView.getContext())
                        .load(fullUrl)
                        .placeholder(R.drawable.default_avatar)
                        .into(ivNewImage);
            } else {
                ivNewImage.setImageResource(R.drawable.default_avatar);
            }
        }
    }
}
