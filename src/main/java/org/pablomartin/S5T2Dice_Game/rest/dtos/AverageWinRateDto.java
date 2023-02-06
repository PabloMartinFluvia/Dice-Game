package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.Utils.PercentageSerializer;

@AllArgsConstructor
@Getter
public class AverageWinRateDto {

    @JsonProperty("Average win rate (ignored player without rolls)")
    @JsonSerialize(using = PercentageSerializer.class)
    private Float average;
}
