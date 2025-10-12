package com.example.blackjack.model.game;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class Deck {
    private final Stack<Card> cards = new Stack<>();

    public Deck() {
        createAndShuffle();
    }

    private void createAndShuffle() {
        this.cards.clear();

        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                if (rank != Rank.HIDDEN) {
                    this.cards.push(new Card(suit, rank));
                }
            }
        }

        Collections.shuffle(this.cards);
    }

    public void reshuffle() {
        createAndShuffle();
    }

    public Card deal() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.pop();
    }

    public int size() {
        return cards.size();
    }
}