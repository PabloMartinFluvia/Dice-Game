package org.pablomartin.S5T2Dice_Game.domain.data;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.game.PlayerDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.RollDetails;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

/*
Adapts domain data vs repositories schema.
Calls CRUD operations from repositories.
 */
public interface GamePersistenceAdapter {

    /**
     * Checks if there's any player stored
     * with the provided ID.
     * @param playerId
     * @return
     */
    boolean existsPlayer(@NotNull UUID playerId);


    default void asserPlayerExists(UUID playerId){
        if(!existsPlayer(playerId)){
            throw new PlayerNotFoundException(playerId);
        }
    }



    /**
     * Goal:
     * Assert the target player exists.
     * Save the roll (linked to the target player).
     * Note: if BD schema accepts it, also increments +1 the number of
     * rolls of the target player.
     * Note: operation not idempotent + many new entities due multiple
     * data sources -> @Transactional
     * @param playerId
     * @param roll
     * @return RollDetails with de dices + instant of roll
     * Note: doesn't contain info if it's a winner or not
     * @throws PlayerNotFoundException
     */
    @Transactional(transactionManager = "chainedTransactionManager")
    RollDetails saveRoll(@NotNull UUID playerId, @NotNull RollDetails roll) throws PlayerNotFoundException;

    /**
     * Goal:
     * Assert the target player exists.
     * Find all rolls (linked to the target player).
     * @param playerId
     * @return collection of RollDetails (can be empty).
     * Each element contain dices + instant of roll. But
     * doesn't contain info if it's a winner or not
     * @throws PlayerNotFoundException
     */
    Collection<RollDetails> findAllRolls(@NotNull UUID playerId) throws PlayerNotFoundException;

    /**
     * Goal:
     * Assert the target player exists.
     * Remove all rolls linked to the target player.
     * Note: also remove/reset all related data IF STORED,
     * like winrate, num of rolls...
     * Note: operation not idempotent -> transactional
     * @param playerId
     * @throws PlayerNotFoundException
     */
    @Transactional(transactionManager = "chainedTransactionManager")
    void deleteAllRolls(@NotNull UUID playerId) throws PlayerNotFoundException;

    /**
     * Goal:
     * Assert the target player exists.
     * Populates the fields that can be read directly (username, id, rolls collection).
     * @param playerId
     * @return
     * @throws PlayerNotFoundException
     */
    PlayerDetails findPlayer(@NotNull UUID playerId) throws PlayerNotFoundException;

    /**
     * Goal:
     * Provide all players, only with fields that can be read directly (username, id, rolls collection).
     * @return
     */
    List<PlayerDetails> findAllPlayers();
}
