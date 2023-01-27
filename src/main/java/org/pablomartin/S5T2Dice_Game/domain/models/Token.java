package org.pablomartin.S5T2Dice_Game.domain.models;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Token {

    private UUID tokenId;

    private Player owner;

    public Token(Player owner) {
        this.owner = owner;
    }
}
