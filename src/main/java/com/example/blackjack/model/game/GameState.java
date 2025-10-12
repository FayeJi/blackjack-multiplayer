package com.example.blackjack.model.game;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;

import java.util.*;

@Data
public class GameState {
    public enum GamePhase {
        WAITING,
        BETTING,
        PLAYER_TURN,
        DEALER_TURN,
        HAND_OVER
    }

    private final String gameId = UUID.randomUUID().toString();
    private String roomName;
    @JsonIgnore
    private final Deck deck = new Deck();

    // Player Information
    private final Map<String, Hand> playerHands = new HashMap<>();
    private final List<String> playerOrder = new ArrayList<>();
    private final Map<String, Boolean> playerStands = new HashMap<>();

    private final Hand dealerHand = new Hand();

    // Game Flow
    private final Map<String, Double> playerBets = new HashMap<>();
    private GamePhase phase = GamePhase.WAITING;
    private String activePlayerUsername;
    private String statusMessage;

    public GameState() {
    }

    public GameState(String roomName) {
        this.roomName = roomName;
        deck.reshuffle();
    }
}