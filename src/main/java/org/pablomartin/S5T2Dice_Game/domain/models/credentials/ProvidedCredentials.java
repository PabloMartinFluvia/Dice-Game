package org.pablomartin.S5T2Dice_Game.domain.models.credentials;

import java.util.UUID;

/*
used for sing up, update username/password, register anonymous
-> settings controller
 */
public interface ProvidedCredentials {

    // username and password (already encoded)

    //when inicialitzated role is also setted

    //if user is authenticated also stores the id

    boolean isUsernameProvided();

    UUID getPlayerId();
    String getUsername();

    void setPlayerId(UUID playerId); //to store it when authenticated



}
