package org.pablomartin.S5T2Dice_Game.domain.models;

/*
Populated from Authentication and/or data loaded from DB.
Used for provide data to generate new JWTs
 */


import java.util.UUID;

public interface SecurityClaims {

    UUID getPlayerId();

    String getUsername();

    Role getRole();

    UUID getRefreshTokenId();

    AccessInfo toAppAccess(String accessJwt, String refreshJwt);

}
