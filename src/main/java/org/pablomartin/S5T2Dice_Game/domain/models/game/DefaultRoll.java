package org.pablomartin.S5T2Dice_Game.domain.models.game;


import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext.getWinValue;

@Builder
@Getter
public class DefaultRoll implements RollDetails {

    private int[] dicesValues;

    private boolean won;

    private LocalDateTime instant;

    @Override
    public void updateIfWon() {
        int sum = 0;
        for(int value : dicesValues){
            sum += value;
        }
        won = sum == getWinValue();
    }

}
