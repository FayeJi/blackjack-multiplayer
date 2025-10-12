package com.example.blackjack.controller;

import com.example.blackjack.dto.UserStatsDto;
import com.example.blackjack.model.User;
import com.example.blackjack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    public ResponseEntity<UserStatsDto> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserStatsDto statsDto = new UserStatsDto();
        statsDto.setUsername(user.getUsername());
        statsDto.setBalance(user.getBalance());
        statsDto.setHandsPlayed(user.getHandsPlayed());
        statsDto.setHandsWon(user.getHandsWon());

        return ResponseEntity.ok(statsDto);
    }
}