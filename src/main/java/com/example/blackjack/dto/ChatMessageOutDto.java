package com.example.blackjack.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ChatMessageOutDto {
    private String sender;
    private String content;
    private long timestamp;
}
