package org.pablomartin.S5T2Dice_Game.domain.models;

import java.util.UUID;


public interface RankedDetails {

    UUID getPlayerId();

    String getUsername();

    float getWinRate();

    int getNumRolls();
}
