package com.yjx.gymmanager.dto;

import lombok.Data;

import java.util.List;

@Data
public class AiChatRequest {
    private String message;
    private List<AiChatMessage> messages;
}
