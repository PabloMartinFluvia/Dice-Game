package org.pablomartin.S5T2Dice_Game.security.basic;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.UUID;

public interface PlayerPrincipalDetails extends UserDetails, CredentialsContainer {

    UUID getPlayerId();

}
