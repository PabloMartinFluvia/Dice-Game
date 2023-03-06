package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.UUID;

public interface JwtService {

    String BEARER_ = "Bearer ";

    /**
     * playerId + username (if registered or admin) + role (if anonymous)
     * @param claims model with data
     * @return bearer token
     */
    String createAccessJwt(SecurityClaims claims);

    /**
     * playerId + refresh token id
     * @param claims model with data
     * @return bearer token
     */
    String createRefreshJwt(SecurityClaims claims);

    boolean isValidAccessJwt(String jwt);

    boolean isValidRefreshJwt(String jwt);

    UUID getUserIdFromAccessJwt(String jwt);

    UUID getUserIdFromRefreshJwt(String jwt);

    UUID getTokenIdFromRefreshJwt(String jwt);

    /**
     *
     * @param jwt bearer token
     * @return can be null
     */
    String getUsernameFromAccessJwt(String jwt);

    /**
     * @param jwt bearer token
     * @return can be null
     */
    Role getRoleFromAccessJwt(String jwt);



}
