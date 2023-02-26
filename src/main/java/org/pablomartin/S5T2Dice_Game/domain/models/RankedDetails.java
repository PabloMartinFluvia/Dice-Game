package org.pablomartin.S5T2Dice_Game.domain.models;

import java.util.UUID;


public interface RankedDetails {

    //playerId, username , win rate

    UUID getPlayerId();

    String getUsername();

    float getWinRate();

    int getNumRolls(); //status details must know the number of rolls done by the user
}
