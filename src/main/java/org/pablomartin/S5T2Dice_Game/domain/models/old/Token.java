package org.pablomartin.S5T2Dice_Game.domain.models.old;

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

    private PlayerOld owner;

    public Token(PlayerOld owner) {
        this.owner = owner;
    }
}
