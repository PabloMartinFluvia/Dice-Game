package org.pablomartin.S5T2Dice_Game.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RollDetails {

    // dices, if is won, instant of roll

    int[] getDicesValues();

    void doResult();

    boolean isWon();


    LocalDateTime getInstantRoll();

    int compareDate(RollDetails other);

    UUID getRollId();
}
