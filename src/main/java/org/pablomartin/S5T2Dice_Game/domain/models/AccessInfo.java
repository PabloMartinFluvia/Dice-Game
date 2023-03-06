package org.pablomartin.S5T2Dice_Game.domain.models;

import java.util.UUID;

/*
Used for populate dto's in responses to inform the user
witch (public) values could provide when requesting
secured resources.
Note: password not included.
 */
public interface AccessInfo {

    UUID getPlayerId();

    String getUsername(); //null, not provided if it's anonymous

    String getAccessJwt();

    String getRefreshJwt();
}
