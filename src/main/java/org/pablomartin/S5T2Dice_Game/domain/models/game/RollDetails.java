package org.pablomartin.S5T2Dice_Game.domain.models.game;

import java.time.LocalDateTime;

public interface RollDetails {

    // dices, if is won, instant of roll

    int[] getDicesValues();

    void updateIfWon();

    boolean isWon();


    LocalDateTime getInstant();
}
