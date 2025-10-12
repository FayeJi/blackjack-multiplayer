package com.example.blackjack.controller;

import com.example.blackjack.dto.GameViewDto;
import com.example.blackjack.model.User;
import com.example.blackjack.model.game.GameState;
import com.example.blackjack.repository.UserRepository;
import com.example.blackjack.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Collection;

@RestController
@RequestMapping("/api/games")
public class GameController {
    @Autowired
    private GameService gameService;
    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<Collection<GameState>> getActiveGames() {
        return ResponseEntity.ok(gameService.getActiveGames());
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<GameState> joinGame(@PathVariable String gameId, Authentication authentication) {
        String username = authentication.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        GameState updatedGame = gameService.joinGame(gameId, user);

        if (updatedGame == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(updatedGame);
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameViewDto> getGameById(@PathVariable String gameId) {
        GameState game = gameService.getGameState(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(GameViewDto.from(game));
    }
}