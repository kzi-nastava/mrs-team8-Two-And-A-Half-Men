package com.project.mobile.service;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.project.mobile.core.WebSocketsMenager.MessageCallback;
import com.project.mobile.core.WebSocketsMenager.WebSocketManager;
import com.project.mobile.core.retrofitClient.RetrofitClient;
import com.project.mobile.models.chat.Message;
import com.project.mobile.models.chat.SendMessageRequest;
import com.project.mobile.models.chat.SupportChat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatService {
    private static ChatService instance;
    private final ChatApi chatApi;
    private final List<SupportChat> chats = new ArrayList<>();
    private final Map<String, MessageCallback> activeCallbacks = new HashMap<>();

    private ChatService() {
        this.chatApi = RetrofitClient.retrofit.create(ChatApi.class);
    }

    public static synchronized ChatService getInstance() {
        if (instance == null) instance = new ChatService();
        return instance;
    }

    // ========== WebSocket Methods ==========

    public void subscribeToChat(Long userId, boolean isAdmin) {
        String topic = isAdmin ?
                "/topic/support/admin" :
                "/topic/chat/" + userId;

        MessageCallback callback = message -> {
            new Handler(Looper.getMainLooper()).post(() -> {
                handleNewMessage(message);
            });
        };

        activeCallbacks.put(topic, callback);
        WebSocketManager.subscribe(topic, callback);
    }

    public void unsubscribeFromChat(Long userId, boolean isAdmin) {
        String topic = isAdmin ?
                "/topic/support/admin" :
                "/topic/chat/" + userId;

        MessageCallback callback = activeCallbacks.remove(topic);
        if (callback != null) {
            WebSocketManager.unsubscribe(topic, callback);
        }
    }

    private void handleNewMessage(String messageJson) {
        try {
            Gson gson = new Gson();
            Message newMessage = gson.fromJson(messageJson, Message.class);

            // Find chat and add message
            boolean chatFound = false;
            for (SupportChat chat : chats) {
                if (Objects.equals(chat.getId(), newMessage.getChatId())) {
                    chat.getMessages().add(newMessage);
                    notifyListeners(chat);
                    chatFound = true;
                    break;
                }
            }

            // Chat not found, fetch it
            if (!chatFound) {
                fetchChatById(newMessage.getChatId(), new ChatCallback() {
                    @Override
                    public void onSuccess(SupportChat chat) {
                        // Already handled in fetchChatById
                    }

                    @Override
                    public void onError(String error) {
                        Log.e("ChatService", "Failed to fetch new chat: " + error);
                    }
                });
            }
        } catch (Exception e) {
            Log.e("ChatService", "Error parsing message", e);
        }
    }

    // ========== API Methods ==========

    public void getMyChat(ChatCallback callback) {
        chatApi.getMyChat().enqueue(new Callback<SupportChat>() {
            @Override
            public void onResponse(Call<SupportChat> call, Response<SupportChat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chats.clear();
                    chats.add(response.body());
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load chat");
                }
            }

            @Override
            public void onFailure(Call<SupportChat> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void getAllActiveChats(ChatsListCallback callback) {
        chatApi.getAllActiveChats().enqueue(new Callback<List<SupportChat>>() {
            @Override
            public void onResponse(Call<List<SupportChat>> call, Response<List<SupportChat>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chats.clear();
                    chats.addAll(response.body());
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to load chats");
                }
            }

            @Override
            public void onFailure(Call<List<SupportChat>> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void fetchChatById(Long chatId, ChatCallback callback) {
        chatApi.getChatById(chatId).enqueue(new Callback<SupportChat>() {
            @Override
            public void onResponse(Call<SupportChat> call, Response<SupportChat> response) {
                if (response.isSuccessful() && response.body() != null) {
                    SupportChat chat = response.body();

                    // Add or update in local list
                    boolean found = false;
                    for (int i = 0; i < chats.size(); i++) {
                        if (Objects.equals(chats.get(i).getId(), chatId)) {
                            chats.set(i, chat);
                            found = true;
                            break;
                        }
                    }

                    if (!found) {
                        chats.add(chat);
                    }

                    notifyListeners(chat);
                    callback.onSuccess(chat);
                } else {
                    callback.onError("Failed to fetch chat");
                }
            }

            @Override
            public void onFailure(Call<SupportChat> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void sendMessage(Long chatId, String content, MessageSentCallback callback) {
        SendMessageRequest request = new SendMessageRequest(content);
        chatApi.sendMessage(chatId, request).enqueue(new Callback<Message>() {
            @Override
            public void onResponse(Call<Message> call, Response<Message> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onError("Failed to send message");
                }
            }

            @Override
            public void onFailure(Call<Message> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    public void markMessagesAsRead(Long chatId, MarkReadCallback callback) {
        chatApi.markMessagesAsRead(chatId).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess();
                } else {
                    callback.onError("Failed to mark as read");
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                callback.onError(t.getMessage());
            }
        });
    }

    // ========== Local Cache Methods ==========

    /**
     * Get chat from local cache (synchronous)
     */
    public SupportChat getChatById(Long chatId) {
        for (SupportChat chat : chats) {
            if (Objects.equals(chat.getId(), chatId)) {
                return chat;
            }
        }
        return null;
    }

    /**
     * Get all chats from local cache (synchronous)
     */
    public List<SupportChat> getAllChats() {
        return new ArrayList<>(chats);
    }

    /**
     * Clear local cache
     */
    public void clearCache() {
        chats.clear();
    }

    // ========== Listener Pattern ==========

    private final List<ChatUpdateListener> listeners = new ArrayList<>();

    public void addListener(ChatUpdateListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(ChatUpdateListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(SupportChat chat) {
        for (ChatUpdateListener listener : new ArrayList<>(listeners)) {
            listener.onChatUpdated(chat);
        }
    }

    // ========== Callbacks ==========

    public interface ChatCallback {
        void onSuccess(SupportChat chat);
        void onError(String error);
    }

    public interface ChatsListCallback {
        void onSuccess(List<SupportChat> chats);
        void onError(String error);
    }

    public interface MessageSentCallback {
        void onSuccess(Message message);
        void onError(String error);
    }

    public interface MarkReadCallback {
        void onSuccess();
        void onError(String error);
    }

    public interface ChatUpdateListener {
        void onChatUpdated(SupportChat chat);
    }
}