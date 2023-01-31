package org.pablomartin.S5T2Dice_Game.security.basic;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface BasicPrincipal extends UserDetails, CredentialsContainer {

    UUID getPlayerId();

    Role getRole(); // to make easier create JWTS based from this principal

}
