package org.pablomartin.S5T2Dice_Game.domain.models;

import java.time.LocalDateTime;
import java.util.UUID;

public interface RollDetails {

    void checkResult();

    boolean isWon();

    int compareDate(RollDetails other);

    UUID getRollId();

    int[] getDicesValues();

    LocalDateTime getInstantRoll();
}
