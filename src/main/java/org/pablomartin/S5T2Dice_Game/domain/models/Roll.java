package org.pablomartin.S5T2Dice_Game.domain.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext.getWinValue;

@Builder
@ToString
@Getter
@EqualsAndHashCode
public class Roll implements RollDetails, Comparable<RollDetails>{

    private UUID rollId; //only used for sorting

    private int[] dicesValues;

    private boolean won;
    private LocalDateTime instantRoll;

    @Override
    public void doResult() {
        int sum = 0;
        for(int value : dicesValues){
            sum += value;
        }
        won = sum == getWinValue();
    }

    @Override
    public int compareDate(RollDetails other) {
        return compareTo(other);
    }

    @Override
    public int compareTo(RollDetails other) {
        int result = this.instantRoll.compareTo(other.getInstantRoll());
        if( result != 0) {
            return result;
        }else {
            //ID used for sorting only when 2 rolls done in the same sec.
            return this.rollId.compareTo(other.getRollId());
        }
    }
}
