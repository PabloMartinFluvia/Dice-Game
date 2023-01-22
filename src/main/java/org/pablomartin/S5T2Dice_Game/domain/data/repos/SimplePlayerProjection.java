package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;


public interface SimplePlayerProjection {

     String getUsername();

     String getPassword();

     Role getRole();
}
