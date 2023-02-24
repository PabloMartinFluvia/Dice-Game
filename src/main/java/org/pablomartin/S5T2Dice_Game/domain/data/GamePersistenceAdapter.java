package org.pablomartin.S5T2Dice_Game.domain.data;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.GameDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/*
Adapts domain data vs repositories schema.
Calls the queries.
*No implements game logic.
 */
public interface GamePersistenceAdapter {

    /**
     * Checks if there's any player stored
     * with the provided ID.
     * @param playerId
     * @return
     */
    boolean existsPlayer(@NotNull UUID playerId);

    /**
     * Goal:
     * Save the roll (linked to the target player).
     * Note: if BD schema accepts it, also increments +1 the number of
     * rolls of the target player.
     * @param playerId
     * @param roll
     * @return RollDetails with de dices + instant of roll
     * Note: doesn't contain info if it's a winner or not
     */
    RollDetails saveRoll(@NotNull UUID playerId, @NotNull RollDetails roll);

    /**
     * Goal:
     * Find all rolls (linked to the target player).
     * @param playerId
     * @return collection of RollDetails (can be empty).
     * Each element contain dices + instant of roll. But
     * doesn't contain info if it's a winner or not
     */
    List<RollDetails> findAllRolls(@NotNull UUID playerId);

    /**
     * Goal:
     * Remove all rolls linked to the target playerid.
     * Note: also remove/reset all related data IF STORED, (in mongo?)
     * like winrate, num of rolls...
     * @param playerId
     */
    void deleteAllRolls(@NotNull UUID playerId);

    /**
     * Goal:
     * Populates the fields that can be read directly (username, id, rolls collection).
     * @param playerId
     * @return Optional empty if player not found
     */
    Optional<GameDetails> findPlayer(@NotNull UUID playerId);

    /**
     * Goal:
     * Provide all players, only with fields that can be read directly (username, id, rolls collection).
     * @return
     */
    List<GameDetails> findAllPlayers();
}
