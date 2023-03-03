package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.utils.PercentageSerializer;

import java.util.UUID;


@Builder
@Getter
public class GameDto { //used only as response


    private UUID playerId;

    private String username;

    @JsonProperty("Number of rolls")
    private int numRolls; //int -> max value is 2.147.483.647, so it'll be enough

    @JsonProperty("Win rate")
    @JsonSerialize(using = PercentageSerializer.class)
    private Float winRate;

}
