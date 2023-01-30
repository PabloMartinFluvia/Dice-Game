package org.pablomartin.S5T2Dice_Game.security.jwt;

import org.springframework.security.core.GrantedAuthority;

import java.io.Serializable;
import java.util.Collection;
import java.util.UUID;

public interface TokenDetails extends Serializable {

    UUID getOwnerId();

}
