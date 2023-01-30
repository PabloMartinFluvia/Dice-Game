package org.pablomartin.S5T2Dice_Game.security.jwt;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.UUID;

public interface RefreshTokenDetails extends TokenDetails {

    UUID getRefreshTokenId();

    String getOwnerUsername();

    Role getOwnerRole();

}
