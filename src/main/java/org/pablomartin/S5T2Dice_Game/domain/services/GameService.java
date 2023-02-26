package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;

import java.util.List;
import java.util.UUID;

public interface GameService {

    //GAME CONTROLLER

    /**
     * Goal: save the roll (linked to the target player).
     * Note: if BD schema accepts it, also increments +1 the number of
     * rolls of the target player.
     * @param targetPlayerId player id
     * @param roll model
     * @return the rolls detail, full populated (dices values, if won, instant)
     */
    RollDetails saveNewRoll(@NotNull UUID targetPlayerId, @NotNull RollDetails roll);

    /**
     * Goal: load the player's roll (Collection of player's rolls)
     * + inform, for each one, if it's a winner one
     * + sort the rolls by instant ASC
     * @param playerId id
     * @return all rolls details, include if the roll is a winner one or not.
     */
    List<RollDetails> loadRollsSorted(@NotNull UUID playerId);

    /**
     * Goal: remove all rolls linked to the target player.
     * Note: also remove/reset all related data IF STORED,
     * like win rate, num of rolls...
     * @param playerId id
     */
    void deleteRolls(@NotNull UUID playerId);

    /**
     * Goal: provide a full populated StatusDetails for the specific player.
     * @param playerId id
     * @return id + username + win rate + num of rolls done. If the player has no rolls done
     * the win rate is 0%.
     */
    RankedDetails loadStatus(@NotNull UUID playerId);

    /**
     * Goal: calculate the average win rate of the players.
     * Note: players without any roll done are ignored.
     * @return the average win rate
     */
    float loadAverageWinRate();

    /**
     * Goal: provide a linked set of players sorted by their
     * average win rate (and number of rolls done).
     * Note: includes ALL players (even those with no rolls done).
     *
     * @return the linked set.
     */
    List<? extends RankedDetails> loadPlayersRanked();
}

