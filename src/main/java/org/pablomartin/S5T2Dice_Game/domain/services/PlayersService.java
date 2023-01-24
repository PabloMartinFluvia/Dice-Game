package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;

import java.util.UUID;

public interface PlayersService {

    Player findPlayerById(UUID playerId);
}