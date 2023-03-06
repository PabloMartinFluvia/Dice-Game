package org.pablomartin.S5T2Dice_Game.domain.models;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.springframework.util.Assert;

import java.util.List;

@Getter
@ToString
@EqualsAndHashCode
public class Game {

    private float winRate;

    private List<RollDetails> rolls;

    public Game(List<RollDetails> rolls) {
        this.rolls = rolls;
    }

    public void calculateWinRate() {
        Assert.isTrue(rolls != null, "Player rolls must be not null.");
        for (RollDetails roll : rolls) {
            roll.checkResult();
        }
        winRate = 0f;
        if(getNumRolls() != 0){
            int winners = (int) rolls.stream()
                    .filter(RollDetails::isWon)
                    .count(); //int -> max value is 2.147.483.647, so it'll be enough
            winRate = ((float) winners) / getNumRolls();
        }
    }

    public int getNumRolls() {
        Assert.isTrue(rolls != null, "Player rolls must be not null.");
        return rolls.size();
    }


}
