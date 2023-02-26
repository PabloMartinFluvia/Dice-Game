package org.pablomartin.S5T2Dice_Game.domain.models;

/*
Populated from Authentication and/or data loaded from DB.
Used for provide data to generate new JWTs
 */


import java.util.UUID;

public interface SecurityClaims {

    /*
    for access jwt: ownerId + username (if registered or admin) / role (if registered or admin)
    for reset jwt: ownerId + tokenId
     */

    /*
    Principal of basic Authentication and refresh Authentication must contain:
    playerId, username , role
     */

    UUID getPlayerId();

    String getUsername();

    Role getRole();

    UUID getRefreshTokenId();

    InfoForAppAccess toAppAccess(String accessJwt, String refreshJwt);

}
