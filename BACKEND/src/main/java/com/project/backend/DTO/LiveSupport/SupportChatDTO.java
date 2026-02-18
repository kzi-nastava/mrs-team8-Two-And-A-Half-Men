package com.project.backend.DTO.LiveSupport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupportChatDTO {
    private Long id;
    private Long userId;
    private String userEmail;
    private String userType;
    private String status;
    private List<MessageDTO> messages;
}