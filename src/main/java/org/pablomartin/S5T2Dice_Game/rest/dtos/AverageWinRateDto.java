package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.Utils.PercentageSerializer;

@AllArgsConstructor
@Getter
public class AverageWinRateDto { //used only as response

    @JsonProperty("Average win rate (players without rolls ignored)")
    @JsonSerialize(using = PercentageSerializer.class)
    private Float average;
}
