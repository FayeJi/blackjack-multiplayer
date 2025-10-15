package com.example.blackjack.service;

import com.example.blackjack.dto.GameViewDto;
import com.example.blackjack.model.User;
import com.example.blackjack.model.game.GameState;
import com.example.blackjack.model.game.Hand;
import com.example.blackjack.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
public class GameService {
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private UserRepository userRepository;

    private static final Map<String, GameState> activeGames = new ConcurrentHashMap<>();
    private static final int MAX_PLAYERS_PER_ROOM = 6;

    public void registerGame(GameState game) {
        if (game != null && !activeGames.containsKey(game.getGameId())) {
            activeGames.put(game.getGameId(), game);
        }
    }

    public GameState joinGame(String gameId, User user) {
        GameState game = activeGames.get(gameId);

        if (game != null && game.getPlayerOrder().size() >= MAX_PLAYERS_PER_ROOM) {
            System.out.println("Attempted to join full game: " + gameId);
            game.setStatusMessage("This table is full!");
            return game;
        }

        if (game != null && !game.getPlayerHands().containsKey(user.getUsername())) {
            game.getPlayerHands().put(user.getUsername(), new Hand());
            game.getPlayerOrder().add(user.getUsername());
            game.getPlayerStands().put(user.getUsername(), false);

            broadcastGameUpdate(gameId, game);
            broadcastLobbyUpdate();
        }
        return game;
    }

    public Collection<GameState> getActiveGames() {
        return activeGames.values();
    }

    public GameState getGameState(String gameId) {
        return activeGames.get(gameId);
    }

    private void broadcastLobbyUpdate() {
        messagingTemplate.convertAndSend("/topic/lobby", getActiveGames());
    }

    public void broadcastGameUpdate(String gameId, GameState gameState) {
        GameViewDto gameView = GameViewDto.from(gameState);
        messagingTemplate.convertAndSend("/topic/game/" + gameId, gameView);
    }

    public void handlePlayerAction(String gameId, String username, String action) {
        GameState game = getGameState(gameId);
        if (game == null) return;

        if (!username.equals(game.getActivePlayerUsername()) && game.getPhase() == GameState.GamePhase.PLAYER_TURN) {
            return;
        }

        switch (action.toLowerCase()) {
            case "start":
                startGame(game);
                break;
            case "deal":
                dealCards(game, username);
                break;
            case "reset":
                resetHand(game);
                break;
            case "hit":
                playerHits(game, username);
                break;
            case "stand":
                playerStands(game, username);
                break;
            case "leave":
                playerLeaves(game, username);
                break;
        }

        if (activeGames.containsKey(gameId)) {
            broadcastGameUpdate(gameId, game);
        }
    }

    private void playerLeaves(GameState game, String username) {
        game.getPlayerHands().remove(username);
        game.getPlayerOrder().remove(username);
        game.getPlayerBets().remove(username);

        game.setStatusMessage(username + " has left the table.");
        System.out.println("Player " + username + " left game " + game.getGameId());

        if (game.getPlayerOrder().isEmpty()) {
            resetGame(game);
            System.out.println("Game " + game.getGameId() + " is now empty and has been reset.");
        }

        broadcastLobbyUpdate();
    }

    private void startGame(GameState game) {
        if (game.getPhase() != GameState.GamePhase.WAITING) return;

        game.setPhase(GameState.GamePhase.BETTING);
        game.setStatusMessage("Betting is open. Host can deal when ready.");
    }

    private void playerHits(GameState game, String username) {
        Hand playerHand = game.getPlayerHands().get(username);
        playerHand.addCard(game.getDeck().deal());

        if (playerHand.getValue() > 21) {
            game.setStatusMessage(username + " busts!");
            moveToNextPlayer(game);
        } else {
            game.setStatusMessage(username + " hits.");
        }
    }

    private void playerStands(GameState game, String username) {
        game.getPlayerStands().put(username, true);
        game.setStatusMessage(username + " stands.");
        moveToNextPlayer(game);
    }

    private void moveToNextPlayer(GameState game) {
        List<String> activePlayersThisHand = game.getPlayerOrder().stream()
                .filter(username -> game.getPlayerBets().containsKey(username) && game.getPlayerBets().get(username) > 0)
                .collect(Collectors.toList());

        int currentPlayerIndex = activePlayersThisHand.indexOf(game.getActivePlayerUsername());

        if (currentPlayerIndex >= 0 && currentPlayerIndex < activePlayersThisHand.size() - 1) {
            String nextPlayer = activePlayersThisHand.get(currentPlayerIndex + 1);
            game.setActivePlayerUsername(nextPlayer);
            game.setStatusMessage("It's now " + nextPlayer + "'s turn.");
            return;
        }

        playDealerTurn(game);
    }

    private void playDealerTurn(GameState game) {
        game.setPhase(GameState.GamePhase.DEALER_TURN);
        game.setActivePlayerUsername(null);

        Hand dealerHand = game.getDealerHand();
        while (dealerHand.getValue() < 17) {
            dealerHand.addCard(game.getDeck().deal());
        }
        game.setStatusMessage("Dealer's turn is over.");
        determineWinners(game);
    }

    private void determineWinners(GameState game) {
        game.setPhase(GameState.GamePhase.HAND_OVER);
        int dealerValue = game.getDealerHand().getValue();
        StringBuilder results = new StringBuilder("Hand over! ");

        for (String username : game.getPlayerOrder()) {
            if (!game.getPlayerBets().containsKey(username)) continue;

            double betAmount = game.getPlayerBets().get(username);
            User user = userRepository.findByUsername(username).orElseThrow();
            int playerValue = game.getPlayerHands().get(username).getValue();

            user.setHandsPlayed(user.getHandsPlayed() + 1);

            if (playerValue > 21) {
                user.setBalance(user.getBalance() - betAmount);
                results.append(username).append(" busted. ");
            } else if (dealerValue > 21 || playerValue > dealerValue) {
                user.setBalance(user.getBalance() + betAmount);
                user.setHandsWon(user.getHandsWon() + 1);
                results.append(username).append(" wins! ");
            } else if (playerValue == dealerValue) {
                results.append(username).append(" pushes. ");
            } else {
                user.setBalance(user.getBalance() - betAmount);
                results.append(username).append(" loses. ");
            }

            userRepository.save(user);
        }
        game.setStatusMessage(results.toString());
    }

    public void handleBet(String gameId, String username, double amount) {
        GameState game = getGameState(gameId);
        if (game == null || game.getPhase() != GameState.GamePhase.BETTING) return;

        User user = userRepository.findByUsername(username).orElseThrow();
        if (user.getBalance() >= amount && amount > 0) {
            game.getPlayerBets().put(username, amount);
            game.setStatusMessage(username + " has placed a bet.");
            broadcastGameUpdate(gameId, game);
        }
    }

    private void dealCards(GameState game, String requestingUser) {
        if (game.getPhase() != GameState.GamePhase.BETTING) return;

        String host = game.getPlayerOrder().get(0);
        if (!requestingUser.equals(host)) {
            return;
        }

        List<String> activePlayersThisHand = game.getPlayerOrder().stream()
                .filter(username -> game.getPlayerBets().containsKey(username) && game.getPlayerBets().get(username) > 0)
                .collect(Collectors.toList());

        if (activePlayersThisHand.isEmpty()) {
            game.setStatusMessage("At least one player must bet before dealing.");
            return;
        }

        for (String username : activePlayersThisHand) {
            if (game.getPlayerHands().get(username).getCards().isEmpty()) {
                game.getPlayerHands().get(username).addCard(game.getDeck().deal());
                game.getPlayerHands().get(username).addCard(game.getDeck().deal());
            }
        }

        if (game.getDealerHand().getCards().isEmpty()) {
            game.getDealerHand().addCard(game.getDeck().deal());
            game.getDealerHand().addCard(game.getDeck().deal());
        }

        game.setPhase(GameState.GamePhase.PLAYER_TURN);
        String firstPlayer = activePlayersThisHand.get(0);
        game.setActivePlayerUsername(firstPlayer);
        game.setStatusMessage("Bets are closed. It's " + firstPlayer + "'s turn.");
    }

    private void resetHand(GameState game) {
        game.getDeck().reshuffle();

        game.getDealerHand().clear();
        game.getPlayerBets().clear();

        for (String username : game.getPlayerOrder()) {
            game.getPlayerHands().get(username).clear();
            game.getPlayerStands().put(username, false);
        }

        game.setPhase(GameState.GamePhase.BETTING);
        game.setActivePlayerUsername(null);
        game.setStatusMessage("New round! Place your bets.");
    }

    private void resetGame(GameState game) {
        game.getPlayerOrder().clear();
        game.getPlayerHands().clear();
        game.getPlayerBets().clear();
        game.getPlayerStands().clear();

        game.getDealerHand().clear();
        game.getDeck().reshuffle();
        game.setPhase(GameState.GamePhase.WAITING);
        game.setActivePlayerUsername(null);
        game.setStatusMessage("Welcome! Waiting for players to join.");
    }
}