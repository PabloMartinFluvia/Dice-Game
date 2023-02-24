package org.pablomartin.S5T2Dice_Game.domain.data.repos.projections;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;

public interface OnlyRole {

    RoleSecurity getSecurityDetails();

    interface RoleSecurity{

        Role getRole();

    }
}
