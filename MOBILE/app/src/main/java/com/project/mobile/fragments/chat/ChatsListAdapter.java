package com.project.mobile.fragments.chat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.R;
import com.project.mobile.models.chat.Message;
import com.project.mobile.models.chat.SupportChat;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ChatsListAdapter extends RecyclerView.Adapter<ChatsListAdapter.ChatViewHolder> {

    private List<SupportChat> chats = new ArrayList<>();
    private OnChatClickListener clickListener;
    private Long selectedChatId = null;

    public interface OnChatClickListener {
        void onChatClick(SupportChat chat);
    }

    public void setChats(List<SupportChat> chats) {
        this.chats = new ArrayList<>(chats);
        notifyDataSetChanged();
    }

    public void updateChat(SupportChat updatedChat) {
        for (int i = 0; i < chats.size(); i++) {
            if (Objects.equals(chats.get(i).getId(), updatedChat.getId())) {
                chats.set(i, updatedChat);
                notifyItemChanged(i);
                return;
            }
        }
        // If chat not found, add it
        chats.add(updatedChat);
        notifyItemInserted(chats.size() - 1);
    }

    public void setOnChatClickListener(OnChatClickListener listener) {
        this.clickListener = listener;
    }

    public void setSelectedChatId(Long chatId) {
        int oldPosition = -1;
        int newPosition = -1;

        // Find old and new positions
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).getId() == selectedChatId) {
                oldPosition = i;
            }
            if (Objects.equals(chats.get(i).getId(), chatId)) {
                newPosition = i;
            }
        }

        selectedChatId = chatId;

        // Notify changes
        if (oldPosition >= 0) {
            notifyItemChanged(oldPosition);
        }
        if (newPosition >= 0) {
            notifyItemChanged(newPosition);
        }
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        SupportChat chat = chats.get(position);
        holder.bind(chat, chat.getId() == selectedChatId);

        holder.itemView.setOnClickListener(v -> {
            if (clickListener != null) {
                clickListener.onChatClick(chat);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {

        private TextView userEmailTextView;
        private TextView userTypeTextView;
        private TextView lastMessageTextView;
        private TextView lastMessageTimeTextView;
        private View unreadIndicator;

        public ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            userEmailTextView = itemView.findViewById(R.id.userEmail);
            userTypeTextView = itemView.findViewById(R.id.userType);
            lastMessageTextView = itemView.findViewById(R.id.lastMessage);
            lastMessageTimeTextView = itemView.findViewById(R.id.lastMessageTime);
            unreadIndicator = itemView.findViewById(R.id.unreadIndicator);
        }

        public void bind(SupportChat chat, boolean isSelected) {
            userEmailTextView.setText(chat.getUserEmail() != null ?
                    chat.getUserEmail() : "Unknown User");
            userTypeTextView.setText(chat.getUserType() != null ?
                    chat.getUserType() : "");

            Message lastMessage = chat.getLastMessage();
            if (lastMessage != null) {
                String preview = lastMessage.getContent();
                if (preview.length() > 50) {
                    preview = preview.substring(0, 50) + "...";
                }
                lastMessageTextView.setText(preview);
                lastMessageTimeTextView.setText(formatTime(lastMessage.getTimestamp()));

                // Show unread indicator if last message is not read by admin
                boolean hasUnread = !lastMessage.getAdminRead() &&
                        lastMessage.getSenderType() != Message.SenderType.ADMIN;
                unreadIndicator.setVisibility(hasUnread ? View.VISIBLE : View.GONE);
            } else {
                lastMessageTextView.setText("No messages yet");
                lastMessageTimeTextView.setText("");
                unreadIndicator.setVisibility(View.GONE);
            }

            // Highlight selected chat
            itemView.setSelected(isSelected);
        }

        private String formatTime(LocalDateTime timestamp) {
            if (timestamp == null) return "";

            Calendar now = Calendar.getInstance();
            Calendar msgTime = Calendar.getInstance();
            msgTime.setTime(Date.from(Instant.from(timestamp)));

            // If today, show time
            if (now.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR) &&
                    now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR)) {
                SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
                return timeFormat.format(timestamp);
            }

            // If yesterday
            now.add(Calendar.DAY_OF_YEAR, -1);
            if (now.get(Calendar.DAY_OF_YEAR) == msgTime.get(Calendar.DAY_OF_YEAR) &&
                    now.get(Calendar.YEAR) == msgTime.get(Calendar.YEAR)) {
                return "Yesterday";
            }

            // Otherwise show date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return dateFormat.format(timestamp);
        }
    }
}
