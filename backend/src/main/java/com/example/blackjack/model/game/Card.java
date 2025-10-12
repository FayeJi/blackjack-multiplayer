package com.example.blackjack.model.game;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Card {
    private final Suit suit;
    private final Rank rank;

    @Override
    public String toString() {
        return rank + " of " + suit;
    }
}