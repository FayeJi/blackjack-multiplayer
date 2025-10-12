package com.example.blackjack.dto;

import lombok.Data;

@Data
public class UserStatsDto {
    private String username;
    private double balance;
    private long handsPlayed;
    private long handsWon;
}