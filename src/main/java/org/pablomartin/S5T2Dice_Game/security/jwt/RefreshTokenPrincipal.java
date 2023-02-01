package org.pablomartin.S5T2Dice_Game.security.jwt;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;

import java.util.UUID;

public interface RefreshTokenPrincipal extends TokenPrincipal {

    UUID getRefreshTokenId();

    String getOwnerUsername();

    Role getOwnerRole();

}