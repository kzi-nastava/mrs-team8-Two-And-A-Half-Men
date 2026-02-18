package com.project.mobile.fragments.chat;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.project.mobile.R;
import com.project.mobile.databinding.FragmentAdminChatsBinding;
import com.project.mobile.models.chat.SupportChat;
import com.project.mobile.service.ChatService;
import com.project.mobile.viewModels.AuthModel;

import java.util.List;

public class AdminChatsFragment extends Fragment implements ChatService.ChatUpdateListener {

    private FragmentAdminChatsBinding binding;
    private ChatsListAdapter chatsListAdapter;
    private ChatService chatService;
    private Long userId;
    private boolean isTwoPane;
    private AuthModel authModel;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatService = ChatService.getInstance();
        authModel = new ViewModelProvider(requireActivity()).get(AuthModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminChatsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        isTwoPane = binding.chatDetailContainer != null;

        binding.progressBar.setVisibility(View.VISIBLE);

        authModel.getMeInfo().thenAccept(meInfo -> {
            if (getActivity() != null) {
                getActivity().runOnUiThread(() -> {
                    if (meInfo != null) {
                        userId = meInfo.getId();
                        initializeChat();
                    } else {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), "User not logged in", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void initializeChat() {
        if (userId == null) {
            binding.progressBar.setVisibility(View.GONE);
            return;
        }

        initViews();
        chatService.addListener(this);
        chatService.subscribeToChat(userId, true);
        loadChats();

        chatsListAdapter.setOnChatClickListener(this::onChatSelected);
    }

    private void initViews() {
        chatsListAdapter = new ChatsListAdapter();
        binding.chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.chatsRecyclerView.setAdapter(chatsListAdapter);
    }

    private void loadChats() {
        chatService.getAllActiveChats(new ChatService.ChatsListCallback() {
            @Override
            public void onSuccess(List<SupportChat> chats) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        if (chats.isEmpty()) {
                            binding.emptyView.setVisibility(View.VISIBLE);
                            binding.chatsRecyclerView.setVisibility(View.GONE);
                        } else {
                            binding.emptyView.setVisibility(View.GONE);
                            binding.chatsRecyclerView.setVisibility(View.VISIBLE);
                            chatsListAdapter.setChats(chats);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    private void onChatSelected(SupportChat chat) {
        if (isTwoPane) {
            showChatInDetailPane(chat);
        } else {
            showChatFullscreen(chat);
        }
    }

    private void showChatInDetailPane(SupportChat chat) {
        ChatFragment chatFragment = ChatFragment.newInstanceForAdmin(chat.getId(), userId);
        getChildFragmentManager().beginTransaction()
                .replace(R.id.chatDetailContainer, chatFragment)
                .commit();

        chatsListAdapter.setSelectedChatId(chat.getId());
    }

    private void showChatFullscreen(SupportChat chat) {
        ChatFragment chatFragment = ChatFragment.newInstanceForAdmin(chat.getId(), userId);

        getParentFragmentManager().beginTransaction()
                .replace(R.id.fragment_container_view_tag, chatFragment)
                .addToBackStack("chat_detail")
                .commit();
    }

    @Override
    public void onChatUpdated(SupportChat chat) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> chatsListAdapter.updateChat(chat));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatService.removeListener(this);
        if (userId != null) {
            chatService.unsubscribeFromChat(userId, true);
        }
    }
}