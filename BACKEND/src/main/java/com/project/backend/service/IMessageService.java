package com.project.backend.service;

import com.project.backend.DTO.LiveSupport.MessageDTO;
import com.project.backend.DTO.LiveSupport.SendMessageRequestDTO;
import com.project.backend.models.AppUser;

import java.util.List;

public interface IMessageService {
    MessageDTO sendMessage(Long chatId, AppUser sender, SendMessageRequestDTO request);

    List<MessageDTO> getChatMessages(Long chatId, AppUser user);

    void markMessagesAsRead(Long chatId, AppUser reader);
}
