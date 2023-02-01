package org.pablomartin.S5T2Dice_Game.domain.models.game;

import lombok.Builder;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.UUID;

@Builder
public class DefaultPlayer implements PlayerDetails{

    private UUID playerId;

    private String username;

    private float winRate;

    private Collection<RollDetails> rolls;

    @Override
    public void updateRollsDetails() {
        Assert.isTrue(rolls != null, "Player rolls must be not null.");
        for (RollDetails roll : rolls) {
            roll.updateIfWon();
        }
    }

    @Override
    public void calculateWinRate() {
        winRate = 0f;
        if(getNumRolls() != 0){
            int winners = (int) rolls.stream()
                    .filter(RollDetails::isWon)
                    .count(); //int -> max value is 2.147.483.647, so it'll be enough
            winRate = ((float) winners) / getNumRolls();
        }
    }

    @Override
    public int getNumRolls() {
        Assert.isTrue(rolls != null, "Player rolls must be not null.");
        return rolls.size();
    }

    @Override
    public float getWinRate() {
        return winRate;
    }
}
