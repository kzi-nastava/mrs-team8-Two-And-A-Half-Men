package com.project.backend.service.impl;

import com.project.backend.DTO.LiveSupport.MessageDTO;
import com.project.backend.DTO.LiveSupport.SupportChatDTO;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.*;
import com.project.backend.models.enums.ChatStatus;
import com.project.backend.repositories.MessageRepository;
import com.project.backend.repositories.SupportChatRepository;
import com.project.backend.service.ISupportChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SupportChatService implements ISupportChatService {

    private final SupportChatRepository chatRepository;
    private final MessageRepository messageRepository;

    @Transactional
    public SupportChatDTO getOrCreateChatForUser(AppUser user) {
        SupportChat chat = chatRepository.findByUser(user)
                .orElseGet(() -> {
                    SupportChat newChat = new SupportChat();
                    newChat.setUser(user);
                    newChat.setStatus(ChatStatus.ACTIVE);
                    return chatRepository.save(newChat);
                });

        return chatToDto(chat);
    }

    @Transactional(readOnly = true)
    public SupportChatDTO getChatById(Long chatId) {
        SupportChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        return chatToDto(chat);
    }

    @Transactional(readOnly = true)
    public List<SupportChatDTO> getAllActiveChats() {
        List<SupportChat> chats = chatRepository.findByStatus(ChatStatus.ACTIVE);

        return chats.stream()
                .map(this::chatToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void closeChat(Long chatId) {
        SupportChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        chat.setStatus(ChatStatus.CLOSED);
        chatRepository.save(chat);
    }

    @Transactional
    public void reopenChat(Long chatId) {
        SupportChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        chat.setStatus(ChatStatus.ACTIVE);
        chatRepository.save(chat);
    }

    private SupportChatDTO chatToDto(SupportChat chat) {
        SupportChatDTO dto = SupportChatDTO.builder()
                .id(chat.getId())
                .userId(chat.getUser().getId())
                .userEmail(chat.getUser().getEmail())
                .userType(chat.getUser().getRole().toString())
                .status(chat.getStatus().toString())
                .build();

        Message lastMessage = messageRepository.findLastMessageByChatId(chat.getId());
        if (lastMessage != null) {
            dto.setLastMessage(messageToDto(lastMessage));
        }

        return dto;
    }

    private MessageDTO messageToDto(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getSender().getId())
                .content(message.getContent())
                .senderType(message.getSender().getRole().toString())
                .adminRead(message.isAdminRead())
                .userRead(message.isUserRead())
                .timestamp(message.getTimestamp())
                .build();
    }
}
