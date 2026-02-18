package com.project.mobile.service;

import com.project.mobile.models.chat.Message;
import com.project.mobile.models.chat.SendMessageRequest;
import com.project.mobile.models.chat.SupportChat;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ChatApi {
    @GET("/api/v1/support/my-chat")
    Call<SupportChat> getMyChat();

    @GET("/api/v1/support/chats")
    Call<List<SupportChat>> getAllActiveChats();

    @GET("/api/v1/support/chats/{chatId}")
    Call<SupportChat> getChatById(@Path("chatId") Long chatId);

    @POST("/api/v1/support/chats/{chatId}/messages")
    Call<Message> sendMessage(
            @Path("chatId") Long chatId,
            @Body SendMessageRequest request
    );

    @PUT("/api/v1/support/chats/{chatId}/mark-read")
    Call<Void> markMessagesAsRead(@Path("chatId") Long chatId);
}
