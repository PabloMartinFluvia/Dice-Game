package org.pablomartin.S5T2Dice_Game.domain.models.game;


import lombok.Builder;

@Builder
public class DefaultRoll implements RollDetails {

    private byte[] dicesValues;

}
