package org.pablomartin.S5T2Dice_Game.rest.dtos;

import lombok.Getter;
import lombok.Setter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.ValidDices;

@Getter
@Setter
public class RollDto {

    @ValidDices
    private byte[] dicesValues;
}
