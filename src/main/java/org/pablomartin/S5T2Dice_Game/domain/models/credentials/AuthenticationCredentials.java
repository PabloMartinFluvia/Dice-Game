package org.pablomartin.S5T2Dice_Game.domain.models.credentials;

/*
Stores the data needed to identify the authenticated user, loaded from
the authentication's principal (used only for basic and refresh).

Used for:
    populate and make available new JWTs
 */

//login (from Basic principal) + access(R) + reset(R)

import java.util.UUID;

public interface AuthenticationCredentials {

    /*
    for access jwt: ownerId + username (if registered or admin) / role (if registered or admin)
    for reset jwt: ownerId + tokenId
     */

    /*
    Principal of basic Auth and refresh Auht must contain:
    playerId, username , role
     */

    //GETTERS:

    // playerId + username + role

    //refresh token Id (for create refresh jwt, setted when persisted)

    AccessDetails toAccessDetails(String accessJwt, String refreshJwt);

    UUID getPlayerId();
}
