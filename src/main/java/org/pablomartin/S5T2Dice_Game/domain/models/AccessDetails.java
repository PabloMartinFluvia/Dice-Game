package org.pablomartin.S5T2Dice_Game.domain.models;

import java.util.UUID;

/*
Model contains data for populate responses that provide access info for authentications
(and for generate the jwts).
 */
public interface AccessDetails {

    //JwtOwnerDetails

    UUID getPlayerId();

    String getUsername();

    Role getRole();

    String getAccessJwt();

    String getRefreshJwt();

    UUID getRefreshTokenId();

}
