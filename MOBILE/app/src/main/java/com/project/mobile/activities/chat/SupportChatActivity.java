package com.project.mobile.activities.chat;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.project.mobile.R;
import com.project.mobile.fragments.chat.ChatFragment;
import com.project.mobile.models.chat.SupportChat;
import com.project.mobile.service.ChatService;

public class SupportChatActivity extends AppCompatActivity {

    private ChatService chatService;
    private Long userId;
    private Long chatId = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support_chat);

        chatService = ChatService.getInstance();
        userId = getUserId(); // from SharedPreferences

        // Subscribe to WebSocket
        chatService.subscribeToChat(userId, false);

        // Load user's chat first
        loadMyChat();
    }

    private void loadMyChat() {
        chatService.getMyChat(new ChatService.ChatCallback() {
            @Override
            public void onSuccess(SupportChat chat) {
                chatId = chat.getId();
                showChatFragment(chat.getId());
            }

            @Override
            public void onError(String error) {
                Toast.makeText(SupportChatActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showChatFragment(Long chatId) {
        ChatFragment chatFragment = ChatFragment.newInstance(chatId, userId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, chatFragment)
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatService.unsubscribeFromChat(userId, false);
    }

    private Long getUserId() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }
}
