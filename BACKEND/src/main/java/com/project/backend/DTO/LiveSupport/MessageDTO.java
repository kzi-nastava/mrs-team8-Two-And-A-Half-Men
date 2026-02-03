package com.project.backend.DTO.LiveSupport;

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
}
