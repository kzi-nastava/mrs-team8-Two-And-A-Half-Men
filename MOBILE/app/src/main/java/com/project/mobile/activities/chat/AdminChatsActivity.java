package com.project.mobile.activities.chat;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.project.mobile.R;
import com.project.mobile.fragments.chat.ChatFragment;
import com.project.mobile.fragments.chat.ChatListFragment;
import com.project.mobile.models.chat.SupportChat;
import com.project.mobile.service.ChatService;

public class AdminChatsActivity extends AppCompatActivity
        implements ChatListFragment.OnChatSelectedListener {

    private ChatService chatService;
    private Long userId;
    private boolean isTwoPane; // true if tablet landscape

    private ChatListFragment chatListFragment;
    private ChatFragment chatFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_chats);

        chatService = ChatService.getInstance();
        userId = getUserId();

        // Check if we're in two-pane mode (tablet landscape)
        isTwoPane = findViewById(R.id.chatDetailContainer) != null;

        // Subscribe to WebSocket
        chatService.subscribeToChat(userId, true);

        // Setup chat list fragment
        if (savedInstanceState == null) {
            chatListFragment = ChatListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.chatListContainer, chatListFragment)
                    .commit();
        }

        getOnBackPressedDispatcher().addCallback(this,
                new OnBackPressedCallback(true) {
                    @Override
                    public void handleOnBackPressed() {
                        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
                            getSupportFragmentManager().popBackStack();
                        } else {
                            setEnabled(false);
                            getOnBackPressedDispatcher().onBackPressed();
                        }
                    }
                });
    }

    @Override
    public void onChatSelected(SupportChat chat) {
        if (isTwoPane) {
            // Tablet landscape - show in detail pane
            showChatInDetailPane(chat);
        } else {
            // Phone or portrait - replace entire view
            showChatFullscreen(chat);
        }
    }

    private void showChatInDetailPane(SupportChat chat) {
        chatFragment = ChatFragment.newInstance(chat.getId(), userId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chatDetailContainer, chatFragment)
                .commit();

        chatListFragment.setSelectedChatId(chat.getId());
    }

    private void showChatFullscreen(SupportChat chat) {
        chatFragment = ChatFragment.newInstance(chat.getId(), userId);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.chatListContainer, chatFragment)
                .addToBackStack("chat_detail")
                .commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        chatService.unsubscribeFromChat(userId, true);
    }

    private Long getUserId() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        return prefs.getLong("user_id", -1);
    }
}
