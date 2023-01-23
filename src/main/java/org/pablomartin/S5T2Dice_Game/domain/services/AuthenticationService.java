package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;

import java.util.UUID;


public interface AuthenticationService {

    public Token performNewSingup(Player player);

    public Token performLogin(String username);

    void invalidateRefreshToken(UUID refreshTokenId);

    void invalidateAllRefreshToken(UUID playerId);
}
