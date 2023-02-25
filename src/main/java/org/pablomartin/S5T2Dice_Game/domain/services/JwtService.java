package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.UUID;

public interface JwtService {

    String BEARER_ = "Bearer ";

    /**
     * playerId + username (if registered or admin) + role (if anonymous)
     * @param credentials
     * @return
     */
    String createAccessJwt(SecurityClaims credentials);

    /**
     * playerId + refresh token id
     * @param credentials
     * @return
     */
    String createRefreshJwt(SecurityClaims credentials);

    boolean isValidAccessJwt(String jwt);

    boolean isValidRefreshJwt(String jwt);

    UUID getUserIdFromAccesJwt(String jwt);

    UUID getUserIdFromRefreshJwt(String jwt);

    UUID getTokenIdFromRefreshJwt(String jwt);

    /**
     *
     * @param jwt
     * @return can be null
     */
    String getUsernameFromAccessJwt(String jwt);

    /**
     * @param jwt
     * @return can be null
     */
    Role getRoleFromAccessJwt(String jwt);



}