package org.pablomartin.S5T2Dice_Game.domain.models.game;

import java.util.UUID;


public interface StatusDetails {

    //playerId, username , win rate

    int getNumRolls(); //status details must know the number of rolls done by the user
}
