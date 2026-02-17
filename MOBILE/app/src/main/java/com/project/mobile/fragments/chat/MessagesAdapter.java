package com.project.mobile.fragments.chat;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.R;
import com.project.mobile.models.chat.Message;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MessageViewHolder> {

    private List<Message> messages = new ArrayList<>();
    private final Long currentUserId;

    private static final int VIEW_TYPE_OWN = 1;
    private static final int VIEW_TYPE_OTHER = 2;

    public MessagesAdapter(Long currentUserId) {
        this.currentUserId = currentUserId;
        Log.d("MessagesAdapter", "📱 Adapter created with userId: " + currentUserId);
    }

    public void setMessages(List<Message> messages) {
        this.messages = new ArrayList<>(messages);
        Log.d("MessagesAdapter", "📨 Messages set: " + messages.size() + " messages");
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messages.get(position);
        boolean isOwn = isOwnMessage(message);

        Log.d("MessagesAdapter", "🔍 Message #" + position +
                " - SenderId: " + message.getSenderId() +
                ", CurrentUserId: " + currentUserId +
                ", IsOwn: " + isOwn);

        return isOwn ? VIEW_TYPE_OWN : VIEW_TYPE_OTHER;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View view;
        if (viewType == VIEW_TYPE_OWN) {
            view = inflater.inflate(R.layout.item_message_own, parent, false);
        } else {
            view = inflater.inflate(R.layout.item_message_other, parent, false);
        }
        return new MessageViewHolder(view);
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
        return message.getSenderId() != null &&
                message.getSenderId().equals(currentUserId);
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {

        private final TextView contentTextView;
        private final TextView timeTextView;
        private final ImageView readIndicator;

        public MessageViewHolder(@NonNull View itemView) {
            super(itemView);
            contentTextView = itemView.findViewById(R.id.messageContent);
            timeTextView = itemView.findViewById(R.id.messageTime);
            readIndicator = itemView.findViewById(R.id.readIndicator);
        }

        public void bind(Message message, boolean isOwnMessage) {
            contentTextView.setText(message.getContent());
            timeTextView.setText(message.getFormattedTime());

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

        private boolean isMessageRead(Message message) {
            switch (message.getSenderType()) {
                case ADMIN:
                    return message.isUserRead();
                case DRIVER:
                case CUSTOMER:
                    return message.isAdminRead();
                default:
                    return false;
            }
        }
    }
}