package com.example.blackjack.controller;

import com.example.blackjack.dto.UserStatsDto;
import com.example.blackjack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/users")
    public ResponseEntity<List<UserStatsDto>> getAllUsers() {
        List<UserStatsDto> users = userRepository.findAll().stream()
                .map(user -> {
                    UserStatsDto dto = new UserStatsDto();
                    dto.setUsername(user.getUsername());
                    dto.setBalance(user.getBalance());
                    dto.setHandsPlayed(user.getHandsPlayed());
                    dto.setHandsWon(user.getHandsWon());
                    return dto;
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(users);
    }
}
