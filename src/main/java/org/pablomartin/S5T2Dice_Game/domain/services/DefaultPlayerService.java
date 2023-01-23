package org.pablomartin.S5T2Dice_Game.domain.services;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultPlayerService implements PlayersService{

    private final PersistenceAdapter persistenceAdapter;

    @Override
    public Player findPlayerById(UUID playerId){
        return persistenceAdapter.findPlayerById(playerId)
                /*
                only throws when client has been authenticated with a valid jwt,
                but the player associated to the "subject" claim it's no (longer) persisted.
                 */
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }
}
