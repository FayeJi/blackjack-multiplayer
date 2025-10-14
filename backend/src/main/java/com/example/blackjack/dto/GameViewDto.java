package com.example.blackjack.dto;

import com.example.blackjack.model.game.Card;
import com.example.blackjack.model.game.GameState;
import com.example.blackjack.model.game.Hand;
import com.example.blackjack.model.game.Rank;
import lombok.Data;
import java.util.List;
import java.util.Map;

@Data
public class GameViewDto {
    private String gameId;
    private String roomName;
    private GameState.GamePhase phase;
    private String activePlayerUsername;
    private String statusMessage;
    private Map<String, Hand> playerHands;
    private Hand dealerHand;
    private List<String> playerOrder;

    public static GameViewDto from(GameState state) {
        GameViewDto view = new GameViewDto();
        view.setGameId(state.getGameId());
        view.setRoomName(state.getRoomName());
        view.setPhase(state.getPhase());
        view.setActivePlayerUsername(state.getActivePlayerUsername());
        view.setStatusMessage(state.getStatusMessage());
        view.setPlayerHands(state.getPlayerHands());
        view.setPlayerOrder(state.getPlayerOrder());

        if (state.getPhase() == GameState.GamePhase.PLAYER_TURN) {
            Hand visibleDealerHand = new Hand();
            visibleDealerHand.addCard(new Card(null, Rank.HIDDEN));
            view.setDealerHand(visibleDealerHand);
        } else {
            view.setDealerHand(state.getDealerHand());
        }

        return view;
    }
}