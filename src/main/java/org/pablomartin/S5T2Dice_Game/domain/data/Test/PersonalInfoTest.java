package org.pablomartin.S5T2Dice_Game.domain.data.Test;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;

import java.util.Collection;
import java.util.Set;

public class PersonalInfoTest {

    String username;

    String password;

    Role role;

    //Set<String> extraAuthorities; //not needed

    Set<RefreshTokenTest> availableRefreshTokens;
}
