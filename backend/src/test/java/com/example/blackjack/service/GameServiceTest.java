package com.example.blackjack.service;

import com.example.blackjack.model.User;
import com.example.blackjack.model.game.*;
import com.example.blackjack.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {
    @Mock
    private SimpMessagingTemplate messagingTemplate;
    @InjectMocks
    private GameService uut;
    @Mock
    private UserRepository userRepository;

    private GameState testGame;
    private String gameId;
    private User player1;
    private User player2;

    @BeforeEach
    void setUp() {
        uut.getActiveGames().clear();
        testGame = new GameState("Test Room");
        uut.registerGame(testGame);
        gameId = testGame.getGameId();

        player1 = new User();
        player1.setUsername("player1");
        player1.setBalance(1000.0);
        player2 = new User();
        player2.setUsername("player2");
        player2.setBalance(1000.0);
    }

    @Test
    void gameStartTest() {
        uut.joinGame(gameId, player1);
        uut.handlePlayerAction(gameId, "player1", "start");
        GameState updatedGame = uut.getGameState(gameId);

        assertEquals(GameState.GamePhase.BETTING, updatedGame.getPhase());
        assertEquals("Betting is open. Host can deal when ready.", updatedGame.getStatusMessage());
    }

    @Test
    void dealCardsTest() {
        uut.joinGame(gameId, player1);
        uut.joinGame(gameId, player2);
        testGame.getPlayerBets().put("player1", 10.0);
        testGame.getPlayerBets().put("player2", 10.0);
        testGame.setPhase(GameState.GamePhase.BETTING);
        uut.handlePlayerAction(gameId, "player1", "deal");
        GameState updatedGame = uut.getGameState(gameId);

        assertEquals(GameState.GamePhase.PLAYER_TURN, updatedGame.getPhase());
        assertEquals("player1", updatedGame.getActivePlayerUsername());
        assertEquals(2, updatedGame.getPlayerHands().get("player1").getCards().size());
        assertEquals(2, updatedGame.getPlayerHands().get("player2").getCards().size());
        assertEquals(2, updatedGame.getDealerHand().getCards().size());
    }

    @Test
    void playerHitTest() {
        uut.joinGame(gameId, player1);
        testGame.getPlayerBets().put("player1", 10.0);
        testGame.setPhase(GameState.GamePhase.PLAYER_TURN);
        testGame.setActivePlayerUsername("player1");
        Hand player1Hand = testGame.getPlayerHands().get("player1");
        player1Hand.addCard(new Card(Suit.HEARTS, Rank.TEN));
        testGame.getDealerHand().addCard(new Card(Suit.CLUBS, Rank.NINE));
        uut.handlePlayerAction(gameId, "player1", "hit");
        GameState updatedGame = uut.getGameState(gameId);

        assertEquals(2, updatedGame.getPlayerHands().get("player1").getCards().size());
        assertEquals(GameState.GamePhase.PLAYER_TURN, updatedGame.getPhase());
    }

    @Test
    void playerBustsTest() {
        uut.joinGame(gameId, player1);
        uut.joinGame(gameId, player2);
        testGame.getPlayerBets().put("player1", 10.0);
        testGame.getPlayerBets().put("player2", 10.0);
        testGame.setPhase(GameState.GamePhase.PLAYER_TURN);
        testGame.setActivePlayerUsername("player1");
        Hand player1Hand = testGame.getPlayerHands().get("player1");
        player1Hand.addCard(new Card(Suit.HEARTS, Rank.TEN));
        player1Hand.addCard(new Card(Suit.SPADES, Rank.TEN));
        testGame.getDealerHand().addCard(new Card(Suit.CLUBS, Rank.NINE));
        uut.handlePlayerAction(gameId, "player1", "hit");
        GameState updatedGame = uut.getGameState(gameId);

        assertTrue(updatedGame.getPlayerHands().get("player1").getValue() > 21, "Player 1 should have busted.");
        assertEquals("player2", updatedGame.getActivePlayerUsername(), "Turn should move to player2 after player1 busts.");
        assertEquals("It's now player2's turn.", updatedGame.getStatusMessage());
    }

    @Test
    void playerStandTest() {
        uut.joinGame(gameId, player1);
        uut.joinGame(gameId, player2);
        testGame.getPlayerBets().put("player1", 10.0);
        testGame.getPlayerBets().put("player2", 10.0);
        testGame.setPhase(GameState.GamePhase.PLAYER_TURN);
        testGame.setActivePlayerUsername("player1");
        testGame.getDealerHand().addCard(new Card(Suit.CLUBS, Rank.NINE));
        uut.handlePlayerAction(gameId, "player1", "stand");
        GameState updatedGame = uut.getGameState(gameId);

        assertEquals("player2", updatedGame.getActivePlayerUsername());
        assertEquals("It's now player2's turn.", updatedGame.getStatusMessage());
    }

    @Test
    void initiateDealerTurnTest() {
        uut.joinGame(gameId, player1);
        testGame.getPlayerBets().put("player1", 10.0);
        testGame.setPhase(GameState.GamePhase.PLAYER_TURN);
        testGame.setActivePlayerUsername("player1");
        testGame.getDealerHand().addCard(new Card(Suit.HEARTS, Rank.TEN));
        testGame.getDealerHand().addCard(new Card(Suit.CLUBS, Rank.SIX));
        when(userRepository.findByUsername("player1")).thenReturn(Optional.of(player1));
        uut.handlePlayerAction(gameId, "player1", "stand");
        GameState updatedGame = uut.getGameState(gameId);

        assertNull(updatedGame.getActivePlayerUsername(), "Active player should be null during dealer's turn.");
        assertTrue(updatedGame.getDealerHand().getCards().size() > 2, "Dealer should have hit on 16.");
    }

    @Test
    void playerLeftGameTest() {
        uut.joinGame(gameId, player1);
        uut.joinGame(gameId, player2);
        uut.handlePlayerAction(gameId, "player1", "leave");
        uut.handlePlayerAction(gameId, "player2", "leave");
        GameState updatedGame = uut.getGameState(gameId);

        assertNull(updatedGame.getActivePlayerUsername());
    }

    @Test
    void playerResetHandTest() {
        uut.joinGame(gameId, player1);
        uut.handlePlayerAction(gameId, "player1", "reset");
        GameState updatedGame = uut.getGameState(gameId);

        assertNull(updatedGame.getActivePlayerUsername());
    }

    @Test
    void gameStartExceptionTest() {
        uut.joinGame(gameId, player1);

        assertThrows(NullPointerException.class, () -> {
            uut.handlePlayerAction("game1", null, "start");
        });
    }
}