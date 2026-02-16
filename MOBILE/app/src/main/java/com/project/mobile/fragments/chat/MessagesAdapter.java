package com.project.mobile.fragments.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.R;
import com.project.mobile.models.chat.Message;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private Long currentUserId;

    private static final int VIEW_TYPE_OWN = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    public MessagesAdapter(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setMessages(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
        notifyDataSetChanged();
    }

    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        return isOwnMessage(message) ? VIEW_TYPE_OWN : VIEW_TYPE_OTHER;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        if (viewType == VIEW_TYPE_OWN) {
            View view = inflater.inflate(R.layout.item_message_own, parent, false);
            return new MessageViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_message_other, parent, false);
            return new MessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        holder.bind(message, isOwnMessage(message));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    private boolean isOwnMessage(Message message) {
        return Objects.equals(message.getSenderId(), currentUserId);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private TextView contentTextView;
        private TextView timeTextView;
        private ImageView readIndicator;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.messageContent);
            timeTextView = itemView.findViewById(R.id.messageTime);
            readIndicator = itemView.findViewById(R.id.readIndicator);
        }

        public void bind(Message message, boolean isOwnMessage) {
            contentTextView.setText(message.getContent());
            timeTextView.setText(formatTime(message.getTimestamp()));

            // Show read indicator only for own messages
            if (readIndicator != null && isOwnMessage) {
                boolean isRead = isMessageRead(message);
                readIndicator.setVisibility(isRead ? View.VISIBLE : View.GONE);

                if (isRead) {
                    readIndicator.setImageResource(R.drawable.ic_check_double);
                    readIndicator.setColorFilter(
                            itemView.getContext().getColor(R.color.read_checkmark)
                    );
                } else {
                    readIndicator.setImageResource(R.drawable.ic_check_single);
                    readIndicator.setColorFilter(
                            itemView.getContext().getColor(R.color.message_time_text)
                    );
                }
            }
        }

        private String formatTime(LocalDateTime timestamp) {
            if (timestamp == null) return "";

            SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            return timeFormat.format(timestamp);
        }

        private boolean isMessageRead(Message message) {
            switch (message.getSenderType()) {
                case ADMIN:
                    return message.getUserRead();
                case DRIVER:
                case CUSTOMER:
                    return message.getAdminRead();
                default:
                    return false;
            }
        }
    }
}