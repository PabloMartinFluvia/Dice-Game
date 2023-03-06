package org.pablomartin.S5T2Dice_Game.security.jwt.providers;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.UUID;

public interface RefreshTokenPrincipal extends TokenPrincipal {

    UUID getRefreshTokenId();

    String getUsername();

    Role getUserRole();

}
