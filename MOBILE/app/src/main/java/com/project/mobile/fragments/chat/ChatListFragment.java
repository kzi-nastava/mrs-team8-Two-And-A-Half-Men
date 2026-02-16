package com.project.mobile.fragments.chat;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.project.mobile.R;
import com.project.mobile.models.chat.SupportChat;
import com.project.mobile.service.ChatService;

import java.util.List;

public class ChatListFragment extends Fragment implements ChatService.ChatUpdateListener {

    private RecyclerView chatsRecyclerView;
    private ChatsListAdapter chatsListAdapter;
    private TextView emptyView;
    private ProgressBar progressBar;

    private ChatService chatService;
    private OnChatSelectedListener listener;

    public interface OnChatSelectedListener {
        void onChatSelected(SupportChat chat);
    }

    public static ChatListFragment newInstance() {
        return new ChatListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        chatService = ChatService.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat_list, container, false);
        initViews(view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        chatService.addListener(this);
        loadChats();

        chatsListAdapter.setOnChatClickListener(chat -> {
            if (listener != null) {
                listener.onChatSelected(chat);
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnChatSelectedListener) {
            listener = (OnChatSelectedListener) context;
        } else {
            throw new RuntimeException(context
                    + " must implement OnChatSelectedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    private void initViews(View view) {
        chatsRecyclerView = view.findViewById(R.id.chatsRecyclerView);
        emptyView = view.findViewById(R.id.emptyView);
        progressBar = view.findViewById(R.id.progressBar);

        chatsListAdapter = new ChatsListAdapter();
        chatsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        chatsRecyclerView.setAdapter(chatsListAdapter);
    }

    private void loadChats() {
        progressBar.setVisibility(View.VISIBLE);

        chatService.getAllActiveChats(new ChatService.ChatsListCallback() {
            @Override
            public void onSuccess(List<SupportChat> chats) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        if (chats.isEmpty()) {
                            emptyView.setVisibility(View.VISIBLE);
                            chatsRecyclerView.setVisibility(View.GONE);
                        } else {
                            emptyView.setVisibility(View.GONE);
                            chatsRecyclerView.setVisibility(View.VISIBLE);
                            chatsListAdapter.setChats(chats);
                        }
                    });
                }
            }

            @Override
            public void onError(String error) {
                if (getActivity() != null) {
                    getActivity().runOnUiThread(() -> {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    });
                }
            }
        });
    }

    @Override
    public void onChatUpdated(SupportChat chat) {
        if (getActivity() != null) {
            getActivity().runOnUiThread(() -> {
                chatsListAdapter.updateChat(chat);
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        chatService.removeListener(this);
    }

    public void setSelectedChatId(Long chatId) {
        chatsListAdapter.setSelectedChatId(chatId);
    }
}
