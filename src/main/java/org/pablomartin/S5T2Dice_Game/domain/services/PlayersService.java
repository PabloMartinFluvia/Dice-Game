package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;

import java.util.UUID;

public interface PlayersService {


    PlayerOld findPlayerById(UUID playerId);

}
