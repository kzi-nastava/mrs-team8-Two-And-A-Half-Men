package com.project.mobile.fragments.chat;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.R;
import com.project.mobile.models.chat.Message;
import com.project.mobile.models.chat.SupportChat;
import com.project.mobile.service.ChatService;

import java.util.Objects;

public class ChatFragment extends Fragment implements ChatService.ChatUpdateListener {

    private RecyclerView messagesRecyclerView;
    private MessagesAdapter messagesAdapter;
    private EditText messageInput;
    private Button sendButton;
    private TextView emptyView;

    private ChatService chatService;
    private SupportChat currentChat;
    private Long userId;
    private Long chatId;

    private static final String ARG_CHAT_ID = "chat_id";
    private static final String ARG_USER_ID = "user_id";

    public static ChatFragment newInstance(Long chatId, Long userId) {
        ChatFragment fragment = new ChatFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_CHAT_ID, chatId);
        args.putLong(ARG_USER_ID, userId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            chatId = getArguments().getLong(ARG_CHAT_ID);
            userId = getArguments().getLong(ARG_USER_ID);
        }
        chatService = ChatService.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatService.addListener(this);
        loadChat();

        sendButton.setOnClickListener(v -> sendMessage());
        messageInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sendButton.setEnabled(!s.toString().trim().isEmpty());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void initViews(View view) {
        messagesRecyclerView = view.findViewById(R.id.messagesRecyclerView);
        messageInput = view.findViewById(R.id.messageInput);
        sendButton = view.findViewById(R.id.sendButton);
        emptyView = view.findViewById(R.id.emptyView);

        messagesAdapter = new MessagesAdapter(userId);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setStackFromEnd(true); // Start from bottom
        messagesRecyclerView.setLayoutManager(layoutManager);
        messagesRecyclerView.setAdapter(messagesAdapter);

        sendButton.setEnabled(false);
    }
    private void loadChat() {
        SupportChat cachedChat = chatService.getChatById(chatId);
        if (cachedChat != null) {
            updateChatUI(cachedChat);
        } else {
            // Fetch from API
            chatService.fetchChatById(chatId, new ChatService.ChatCallback() {
                @Override
                public void onSuccess(SupportChat chat) {
                    updateChatUI(chat);
                }

                @Override
                public void onError(String error) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateChatUI(SupportChat chat) {
        currentChat = chat;
        if (chat.getMessages().isEmpty()) {
            emptyView.setVisibility(View.VISIBLE);
            messagesRecyclerView.setVisibility(View.GONE);
        } else {
            emptyView.setVisibility(View.GONE);
            messagesRecyclerView.setVisibility(View.VISIBLE);
            messagesAdapter.setMessages(chat.getMessages());
            scrollToBottom();
        }
    }

    private void sendMessage() {
        String content = messageInput.getText().toString().trim();
        if (content.isEmpty() || currentChat == null) return;

        // Disable input while sending
        sendButton.setEnabled(false);
        messageInput.setEnabled(false);

        chatService.sendMessage(currentChat.getId(), content, new ChatService.MessageSentCallback() {
            @Override
            public void onSuccess(Message message) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        messageInput.setText("");
                        messageInput.setEnabled(true);
                        // Message will be added via WebSocket callback
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        messageInput.setEnabled(true);
                        sendButton.setEnabled(true);
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onChatUpdated(SupportChat chat) {
        if (currentChat != null && Objects.equals(chat.getId(), currentChat.getId())) {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    updateChatUI(chat);
                });
            }
        }
    }

    private void scrollToBottom() {
        if (messagesAdapter.getItemCount() > 0) {
            messagesRecyclerView.smoothScrollToPosition(messagesAdapter.getItemCount() - 1);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatService.removeListener(this);
    }

    public Long getChatId() {
        return chatId;
    }
}
