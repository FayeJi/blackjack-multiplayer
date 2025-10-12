package com.example.blackjack.controller;

import com.example.blackjack.dto.ChatMessageInDto;
import com.example.blackjack.dto.ChatMessageOutDto;
import com.example.blackjack.dto.PlayerActionDto;
import com.example.blackjack.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import com.example.blackjack.dto.PlayerBetDto;

import java.security.Principal;

@Controller
public class GamePlayController {
    @Autowired
    private GameService gameService;

    @MessageMapping("/game/{gameId}/action")
    public void handlePlayerAction(@DestinationVariable String gameId, PlayerActionDto action, Principal principal) {
        // 'Principal' is automatically populated by Spring Security with the authenticated user's info
        String username = principal.getName();
        gameService.handlePlayerAction(gameId, username, action.getAction());
    }

    @MessageMapping("/game/{gameId}/bet")
    public void handlePlayerBet(@DestinationVariable String gameId, PlayerBetDto bet, Principal principal) {
        String username = principal.getName();
        gameService.handleBet(gameId, username, bet.getAmount());
    }

    @MessageMapping("/game/{gameId}/chat")
    @SendTo("/topic/game/{gameId}/chat")
    public ChatMessageOutDto handleChatMessage(ChatMessageInDto message, Principal principal) {
        String username = principal.getName();
        return new ChatMessageOutDto(username, message.getContent(), System.currentTimeMillis());
    }
}