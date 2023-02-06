package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.pablomartin.S5T2Dice_Game.Utils.PercentageSerializer;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.NullableValidUsername;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;



@Builder
@Getter
public class PlayerDto {


    private UUID playerId;

    private String username;

    @JsonProperty("Number of rolls")
    private int numRolls; //int -> max value is 2.147.483.647, so it'll be enough

    @JsonProperty("Win rate")
    @JsonSerialize(using = PercentageSerializer.class)
    private Float winRate;

}
