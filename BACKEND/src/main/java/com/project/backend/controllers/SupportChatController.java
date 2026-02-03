package com.project.backend.controllers;

import com.project.backend.DTO.LiveSupport.MessageDTO;
import com.project.backend.DTO.LiveSupport.SendMessageRequestDTO;
import com.project.backend.DTO.LiveSupport.SupportChatDTO;
import com.project.backend.models.AppUser;
import com.project.backend.service.IMessageService;
import com.project.backend.service.ISupportChatService;
import com.project.backend.util.AuthUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/support")
@RequiredArgsConstructor
public class SupportChatController {

    private final ISupportChatService chatService;
    private final IMessageService messageService;
    private final AuthUtils authUtils;

    @GetMapping("/my-chat")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'DRIVER')")
    public ResponseEntity<SupportChatDTO> getMyChat() {
        AppUser currentUser = authUtils.getCurrentUser();
        SupportChatDTO chat = chatService.getOrCreateChatForUser(currentUser);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/chats")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SupportChatDTO>> getAllActiveChats() {
        List<SupportChatDTO> chats = chatService.getAllActiveChats();
        return ResponseEntity.ok(chats);
    }

    @GetMapping("/chats/{chatId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<SupportChatDTO> getChatById(@PathVariable Long chatId) {
        SupportChatDTO chat = chatService.getChatById(chatId);
        return ResponseEntity.ok(chat);
    }

    @GetMapping("/chats/{chatId}/messages")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<List<MessageDTO>> getChatMessages(@PathVariable Long chatId) {
        AppUser currentUser = authUtils.getCurrentUser();
        List<MessageDTO> messages = messageService.getChatMessages(chatId, currentUser);
        return ResponseEntity.ok(messages);
    }

    @PostMapping("/chats/{chatId}/messages")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<MessageDTO> sendMessage(
            @PathVariable Long chatId,
            @Valid @RequestBody SendMessageRequestDTO request) {
        AppUser currentUser = authUtils.getCurrentUser();
        MessageDTO message = messageService.sendMessage(chatId, currentUser, request);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/chats/{chatId}/mark-read")
    @PreAuthorize("hasAnyRole('ADMIN', 'CUSTOMER', 'DRIVER')")
    public ResponseEntity<Void> markMessagesAsRead(@PathVariable Long chatId) {
        AppUser currentUser = authUtils.getCurrentUser();
        messageService.markMessagesAsRead(chatId, currentUser);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/chats/{chatId}/close")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> closeChat(@PathVariable Long chatId) {
        chatService.closeChat(chatId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/chats/{chatId}/reopen")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> reopenChat(@PathVariable Long chatId) {
        chatService.reopenChat(chatId);
        return ResponseEntity.ok().build();
    }
}
