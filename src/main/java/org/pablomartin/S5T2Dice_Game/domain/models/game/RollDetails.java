package org.pablomartin.S5T2Dice_Game.domain.models.game;

public interface RollDetails {

    // dices, if is won, instant

    void updateIfWon();

    boolean isWon();


}
