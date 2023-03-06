package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.utils.PercentageSerializer;

@AllArgsConstructor
@Getter
public class AverageWinRateDto { //used only as response


    @JsonProperty("Average win rate")
    @JsonSerialize(using = PercentageSerializer.class)
    private Float average;
}
