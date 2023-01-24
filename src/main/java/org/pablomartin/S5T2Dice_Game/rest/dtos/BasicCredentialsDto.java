package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.FullPopulated;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.NullableValidPassword;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.NullableValidUsername;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
public class BasicCredentialsDto {

    @NullableValidUsername//default grup, aplies always
    @NotNull(groups = FullPopulated.class)
    private String username;

    @JsonProperty(access = WRITE_ONLY)
    @NullableValidPassword//default group, aplies always
    @NotNull(groups = FullPopulated.class)
    private String password;

    /*
    singup & registerBasicCredentials -> required username + password
    updateBasicCredentials -> both are optional
     */
}
