package org.pablomartin.S5T2Dice_Game.domain.services.old;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.old.PersistenceAdapterV2;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultPlayerService implements PlayersService{

    private final PersistenceAdapterV2 persistenceAdapterV2;


    private void asserPlayerExists(UUID playerId){
        if(!persistenceAdapterV2.existsPlayer(playerId)){
            throw new PlayerNotFoundException(playerId);
        }
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public PlayerOld findPlayerById(UUID playerId){
        return persistenceAdapterV2.findPlayerById(playerId)
                /*
                only throws when client has been authenticated with a valid jwt,
                but the player associated to the "subject" claim it's no (longer) persisted.
                 */
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
    }
}
