package org.pablomartin.S5T2Dice_Game.domain.services.old;

import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;

import java.util.Set;
import java.util.UUID;

public interface JwtService {

    static final String BEARER_ = "Bearer ";

    String[] generateJwts(Token refreshToken);

    String generateAccessJwt(PlayerOld playerOld);

    boolean isValidAccessJwt(String jwt);

    boolean isValidRefreshJwt(String jwt);

    UUID getUserIdFromAccesJwt (String jwt);

    Role getUserRoleFormAccessJwt(String jwt);

    UUID getUserIdFromRefreshJwt (String jwt);

    Set<String> getUserAuthoritiesFromAccesJwt(String jwt);

    UUID getTokenIdFromRefreshJwt (String jwt);

}
