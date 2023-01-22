package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.RefreshToken;

import java.util.UUID;

public interface JwtService {

    String[] generateJwts(RefreshToken refreshToken);
}
