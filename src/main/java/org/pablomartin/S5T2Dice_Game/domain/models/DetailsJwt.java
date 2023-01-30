package org.pablomartin.S5T2Dice_Game.domain.models;

import java.util.UUID;

/*
Responsibility:
Store (and manipulate) the data associated to the user required for generate access JWTS
 */
public interface DetailsJwt {

    UUID getOwnerId();

    String getOwnerUsername();

    String getOwnerRole();
}
