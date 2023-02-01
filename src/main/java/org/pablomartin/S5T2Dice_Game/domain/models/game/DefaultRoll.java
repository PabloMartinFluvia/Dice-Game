package org.pablomartin.S5T2Dice_Game.domain.models.game;


import lombok.Builder;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext.getWonValue;

@Builder
public class DefaultRoll implements RollDetails {

    private byte[] dicesValues;

    boolean won;

    @Override
    public void updateIfWon() {
        int sum = 0;
        for(byte value : dicesValues){
            sum += value;
        }
        won = sum == getWonValue() ? true : false;
    }

    @Override
    public boolean isWon() {
        return won;
    }
}
