package org.pablomartin.S5T2Dice_Game.domain.models;

/*
Responsibility:
Store (and manipulate) the data associated to the player's username and password
 */
public interface BasicCredentials {

    //instances must have access to username and
    // password (always set encoded)

    String getUsername();

    String getPassword();
}
