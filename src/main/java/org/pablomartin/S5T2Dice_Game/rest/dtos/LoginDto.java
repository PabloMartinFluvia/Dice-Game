package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.ValidPassword;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.ValidUsername;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
public class LoginDto {

    @ValidUsername
    private String username;

    @JsonProperty(access = WRITE_ONLY)
    @ValidPassword
    private String password;
}
