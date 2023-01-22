package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.RefreshToken;


public interface AuthenticationService {

    public RefreshToken performNewSingup(Player player);

    public RefreshToken performLogin(String username);
}
