package org.pablomartin.S5T2Dice_Game.security.jwt.providers;

import java.io.Serializable;
import java.util.UUID;

public interface TokenPrincipal extends Serializable {

    UUID getUserId();

}
