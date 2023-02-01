package org.pablomartin.S5T2Dice_Game.domain.models.game;

public interface PlayerDetails extends StatusDetails{
    

    //inherited: playerId, username , win rate
    
    //also the rolls collection (can be empty)

    void updateRollsDetails();

    void calculateWinRate();

    float getWinRate();
}
