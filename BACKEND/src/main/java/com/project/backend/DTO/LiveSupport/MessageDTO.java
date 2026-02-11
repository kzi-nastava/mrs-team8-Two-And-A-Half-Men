package com.project.backend.DTO.LiveSupport;

import com.project.backend.models.Message;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private Long chatId;
    private Long senderId;
    private String content;
    private String senderType;
    private Boolean adminRead;
    private Boolean userRead;
    private LocalDateTime timestamp;

    public MessageDTO(Message message) {
        this.id = message.getId();
        this.chatId = message.getChat().getId();
        this.senderId = message.getSender().getId();
        this.content = message.getContent();
        this.senderType = message.getSender().getRole().name();
        this.adminRead = message.isAdminRead();
        this.userRead = message.isUserRead();
        this.timestamp = message.getTimestamp();
    }
}
