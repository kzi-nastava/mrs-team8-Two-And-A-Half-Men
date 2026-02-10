package com.project.backend.service.impl;

import com.project.backend.DTO.LiveSupport.MessageDTO;
import com.project.backend.DTO.LiveSupport.SendMessageRequestDTO;
import com.project.backend.exceptions.BadRequestException;
import com.project.backend.exceptions.ResourceNotFoundException;
import com.project.backend.models.Admin;
import com.project.backend.models.AppUser;
import com.project.backend.models.Message;
import com.project.backend.models.SupportChat;
import com.project.backend.models.enums.UserRole;
import com.project.backend.repositories.MessageRepository;
import com.project.backend.repositories.SupportChatRepository;
import com.project.backend.service.IMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageService implements IMessageService {

    private final MessageRepository messageRepository;
    private final SupportChatRepository chatRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public MessageDTO sendMessage(Long chatId, AppUser sender, SendMessageRequestDTO request) {
        SupportChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        if (!isUserAllowedToSendMessage(chat, sender)) {
            throw new BadRequestException("User not allowed to send messages in this chat");
        }

        Message message = Message.builder()
                .chat(chat)
                .sender(sender)
                .content(request.getContent())
                .timestamp(LocalDateTime.now())
                .adminRead(sender.getRole() == UserRole.ADMIN)
                .userRead(sender.getRole() != UserRole.ADMIN)
                .build();

        message = messageRepository.save(message);

        MessageDTO messageDTO = messageToDto(message);

        broadcastMessage(chat, messageDTO);

        return messageDTO;
    }

    @Transactional(readOnly = true)
    public List<MessageDTO> getChatMessages(Long chatId, AppUser user) {
        SupportChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId +" not found"));

        if (!isUserAllowedToAccessChat(chat, user)) {
            throw new BadRequestException("Access denied");
        }

        List<Message> messages = messageRepository.findByChatIdOrderByTimestamp(chatId);
        return messages.stream()
                .map(this::messageToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markMessagesAsRead(Long chatId, AppUser reader) {
        SupportChat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new ResourceNotFoundException("Chat with id " + chatId + " not found"));

        if (!isUserAllowedToAccessChat(chat, reader)) {
            throw new BadRequestException("Access denied");
        }

        if (reader.getRole() == UserRole.ADMIN)
            messageRepository.markAdminRead(chatId);
        else
            messageRepository.markUserRead(chatId);
    }

    private void broadcastMessage(SupportChat chat, MessageDTO messageDTO) {
        messagingTemplate.convertAndSend(
                "/topic/chat/" + chat.getId(),
                messageDTO
        );

        messagingTemplate.convertAndSend("/topi c/support/admin", messageDTO);
    }

    private boolean isUserAllowedToSendMessage(SupportChat chat, AppUser sender) {
        if (sender instanceof Admin) {
            return true;
        }

        return chat.getUser().getId().equals(sender.getId());
    }

    private boolean isUserAllowedToAccessChat(SupportChat chat, AppUser user) {
        if (user instanceof Admin) {
            return true;
        }
        return chat.getUser().getId().equals(user.getId());
    }

    private MessageDTO messageToDto(Message message) {
        return MessageDTO.builder()
                .id(message.getId())
                .chatId(message.getChat().getId())
                .senderId(message.getSender().getId())
                .senderType(message.getSender().getRole().toString())
                .adminRead(message.isAdminRead())
                .userRead(message.isUserRead())
                .timestamp(message.getTimestamp())
                .build();
    }
}
