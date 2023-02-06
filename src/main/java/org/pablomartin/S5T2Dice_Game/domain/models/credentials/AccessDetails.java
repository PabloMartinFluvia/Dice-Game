package org.pablomartin.S5T2Dice_Game.domain.models.credentials;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;

import java.util.UUID;

/*
Model contains data for provide info to responses to inform
the user how provide authentications when requesting
secured resources.
Note: password not included.
 */
public interface AccessDetails {

    //playerId, username (null if the username stored is the default)
    //Strings: access jwt + refresh jwt

    UUID getPlayerId();

    String getUsername();

    String getAccessJwt();

    String getRefreshJwt();
}
