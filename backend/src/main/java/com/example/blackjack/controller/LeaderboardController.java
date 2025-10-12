package com.example.blackjack.controller;

import com.example.blackjack.dto.UserStatsDto;
import com.example.blackjack.model.User;
import com.example.blackjack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/leaderboard")
public class LeaderboardController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<UserStatsDto>> getLeaderboard() {
        List<User> topUsers = userRepository.findTop10LeaderboardUsersByWinRate();

        List<UserStatsDto> leaderboardData = topUsers.stream()
                .map(user -> {
                    UserStatsDto dto = new UserStatsDto();
                    dto.setUsername(user.getUsername());
                    dto.setBalance(user.getBalance());
                    dto.setHandsPlayed(user.getHandsPlayed());
                    dto.setHandsWon(user.getHandsWon());
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(leaderboardData);
    }
}