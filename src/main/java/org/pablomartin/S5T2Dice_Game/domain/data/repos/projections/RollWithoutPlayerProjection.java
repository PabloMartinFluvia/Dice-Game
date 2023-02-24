package org.pablomartin.S5T2Dice_Game.domain.data.repos.projections;

import lombok.Value;
import org.pablomartin.S5T2Dice_Game.domain.models.Roll;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;

import java.time.LocalDateTime;
import java.util.UUID;

@Value //immutable entity
public class RollWithoutPlayerProjection {

    //projection for avoid loading player data each time one roll is load from DB

    UUID rollId;
    int[] dicesValues;

    LocalDateTime instantRoll;

    public RollDetails toRollDetails(){
        return Roll.builder()
                .rollId(rollId) //loaded as auxiliar data when sorted
                .dicesValues(dicesValues)
                //no info if won
                .instantRoll(instantRoll)
                .build();
    }
}
