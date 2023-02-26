package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;

import java.util.UUID;

public abstract class AbstractService {

    protected void assertPlayerExists(@NotNull UUID playerId){
        if(!existsPlayer(playerId)){
            throw new PlayerNotFoundException(playerId);
        }
    }

    protected abstract boolean existsPlayer (@NotNull UUID playerId);
}
