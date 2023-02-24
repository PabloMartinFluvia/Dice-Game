package org.pablomartin.S5T2Dice_Game.domain.services.old;

import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.models.old.TokenOld;

import java.util.UUID;


public interface AuthenticationServiceOld {

    void invalidateRefreshToken(UUID refreshTokenId);

    void invalidateAllRefreshToken(UUID ownerId);

    TokenOld allowNewRefreshTokenFromLogin(String username);

    TokenOld resetRefreshTokens(UUID ownerId);

    TokenOld performSingup(PlayerOld playerOld);

    PlayerOld uptadeBasicCredentials(PlayerOld credentialsProvider);



}
