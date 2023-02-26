package org.pablomartin.S5T2Dice_Game.domain.models;

import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;

import java.util.Optional;
import java.util.UUID;

/*
used for sing up, update username/password, register anonymous
-> settings controller
 */
public interface NewPlayerInfo {

    boolean isUsernameProvided();

    @Nullable
    String getUsername(); // maybe default or provided. Null when ***

    @Nullable
    String getPasswordEncoded(); // maybe null if singup as anonymous or when ***

    //***: registered user wants to update basic credentials without updating this field.

    void setPlayerId(UUID playerId); //set when player is authenticated

    //not empty when player is authenticated
    Optional<UUID> getPlayerAuthenticatedId();

    @NonNull
    Role getRole(); // set when instance is created

}
