package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AuthenticationCredentials;

public interface JwtService {

    String createAccessJwt(AuthenticationCredentials credentials);

    String createRefreshJwt(AuthenticationCredentials credentials);
}
