package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;

import java.util.UUID;


public interface AuthenticationService {

    Token performNewSingup(Player player);

    Player uptadeBasicCredentials(Player source);

    Token performLogin(String username);

    Token performReset(Player player);

    void invalidateRefreshToken(UUID refreshTokenId);

    void invalidateAllRefreshToken(Player player);

}
