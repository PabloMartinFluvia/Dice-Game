package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;

import java.util.UUID;


public interface AuthenticationService {

    void invalidateRefreshToken(UUID refreshTokenId);

    void invalidateAllRefreshToken(UUID ownerId);

    Token allowNewRefreshTokenFromLogin(String username);

    Token resetRefreshTokens(UUID ownerId);

    Token performSingup(PlayerOld playerOld);

    PlayerOld uptadeBasicCredentials(PlayerOld credentialsProvider);



}
