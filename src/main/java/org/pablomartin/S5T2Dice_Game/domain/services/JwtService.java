package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.Token;

import java.util.Set;
import java.util.UUID;

public interface JwtService {

    public static final String BEARER_ = "Bearer ";

    String[] generateJwts(Token refreshToken);

    boolean isValidAccessJwt(String jwt);

    boolean isValidRefreshJwt(String jwt);

    UUID getUserIdFromAccesJwt (String jwt);

    UUID getUserIdFromRefreshJwt (String jwt);

    Set<String> getUserAuthoritiesFromAccesJwt(String jwt);

    UUID getTokenIdFromRefreshJwt (String jwt);
}
