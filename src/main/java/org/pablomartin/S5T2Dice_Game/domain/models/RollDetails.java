package org.pablomartin.S5T2Dice_Game.domain.models;

import java.time.LocalDateTime;

/*
Model contains data for store roll details and manipulate it.
 */
public interface RollDetails {

    byte[] getDices();

    boolean isWon();

    LocalDateTime getRollInstant();
}
