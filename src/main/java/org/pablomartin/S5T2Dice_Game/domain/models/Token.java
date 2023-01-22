package org.pablomartin.S5T2Dice_Game.domain.models;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Token {

    private UUID tokenId;

    private Player owner;

    public Token(Player owner) {
        this.owner = owner;
    }
}
