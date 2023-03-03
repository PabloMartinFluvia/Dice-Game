package org.pablomartin.S5T2Dice_Game.security.basic;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface BasicPrincipal extends UserDetails {

    UUID getUserId(); // to make easier create JWTS based from this principal

    Role getUserRole(); // to make easier create JWTS based from this principal

}
