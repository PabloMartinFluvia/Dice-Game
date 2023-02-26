package org.pablomartin.S5T2Dice_Game.domain.models.old;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.getWinValue;

@Builder
@Getter
public class RollOld {// implements RollDetails {

    private int[] dicesValues;

    private boolean won;

    private LocalDateTime instantRoll;

    //@Override
    public void updateIfWon() {
        int sum = 0;
        for(int value : dicesValues){
            sum += value;
        }
        won = sum == getWinValue();
    }

}
