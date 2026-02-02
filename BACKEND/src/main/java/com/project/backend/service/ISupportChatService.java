package com.project.backend.service;

import com.project.backend.DTO.LiveSupport.SupportChatDTO;
import com.project.backend.models.AppUser;

import java.util.List;

public interface ISupportChatService {
    SupportChatDTO getOrCreateChatForUser(AppUser user);

    SupportChatDTO getChatById(Long chatId);

    List<SupportChatDTO> getAllActiveChats();

    void closeChat(Long chatId);

    void reopenChat(Long chatId);
}
