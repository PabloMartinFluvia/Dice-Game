package org.pablomartin.S5T2Dice_Game.domain.models;

import java.util.UUID;

/*
Responsibility:
Store (and manipulate) the data associated to the user ranking
 */
public interface StatusDetails {

    UUID getPlayerId();

    String getUsername();

    long getNumRolls();

    float getWinRate();
}
