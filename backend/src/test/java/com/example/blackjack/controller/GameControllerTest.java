package com.example.blackjack.controller;

import com.example.blackjack.dto.GameViewDto;
import com.example.blackjack.model.User;
import com.example.blackjack.model.game.GameState;
import com.example.blackjack.repository.UserRepository;
import com.example.blackjack.service.GameService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import java.util.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class GameControllerTest {
    @Mock
    private GameService mockGameService;
    @Mock
    private UserRepository mockUserRepository;
    @InjectMocks
    private GameController uut;


    @Test
    void joinGameTest() {
        User mockUser = new User();
        mockUser.setId(123L);
        String expectedUserName = "user1";
        mockUser.setUsername(expectedUserName);

        Authentication mockAuthentication = mock(Authentication.class);
        when(mockAuthentication.getName()).thenReturn(expectedUserName);

        GameState gameState = new GameState();
        when(mockGameService.joinGame(anyString(), eq(mockUser))).thenReturn(gameState);
        ResponseEntity<GameState> actualResponse = uut.joinGame("gameId", mockAuthentication);
    }

    @Test
    void getGameByIdTest() {
        GameState gameState = new GameState("Table 1");
        when(mockGameService.getGameState(eq("gameId"))).thenReturn(gameState);
        ResponseEntity<GameViewDto> actualResponse = uut.getGameById("gameId");

        assertEquals("Table 1", actualResponse.getBody().getRoomName());
    }

    @Test
    void getActiveGamesTest() {
        GameState gameState1 = new GameState("Table 1");
        GameState gameState2 = new GameState("Table 2");
        List<GameState> gameState = new ArrayList<>();
        gameState.add(gameState1);
        gameState.add(gameState2);
        when(mockGameService.getActiveGames()).thenReturn(gameState);
        ResponseEntity<Collection<GameState>> actualResponse = uut.getActiveGames();

        assertEquals(2, actualResponse.getBody().size());
    }

}
