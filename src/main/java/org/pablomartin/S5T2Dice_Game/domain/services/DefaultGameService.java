package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.GamePersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.GameDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DefaultGameService extends AbstractService implements GameService {

    private  final GamePersistenceAdapter adapter;

    @Override
    protected boolean existsPlayer(@NotNull UUID playerId) {
        return adapter.existsPlayer(playerId);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public RollDetails saveNewRoll(@NotNull UUID targetPlayerId, @NotNull RollDetails roll) {
        assertPlayerExists(targetPlayerId);
        roll =adapter.saveRoll(targetPlayerId,roll);
        roll.doResult();
        return roll;
    }

    @Override
    public List<RollDetails> loadRollsSorted(@NotNull UUID playerId) {
        assertPlayerExists(playerId);
        List<RollDetails> rolls = adapter.findAllRolls(playerId);
        rolls.forEach(RollDetails::doResult);
        rolls.sort(Comparator.comparing(RollDetails::getInstantRoll));
        return rolls;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void deleteRolls(@NotNull UUID playerId) {
        adapter.deleteAllRolls(playerId);
    }

    @Override
    public RankedDetails loadStatus(@NotNull UUID playerId) {
        GameDetails player = adapter.findPlayer(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        updatePlayerDetails(player);
        return player;
    }

    private void updatePlayerDetails(@NotNull GameDetails player){
        player.calculateWinRate();
    }

    private List<GameDetails> loadAllPlayerDetails(){
        List<GameDetails> players = adapter.findAllPlayers();
        players.forEach(this::updatePlayerDetails);
        return players;
    }

    @Override
    public float loadAverageWinRate() {
        List<GameDetails> players = loadAllPlayerDetails();
        players.removeIf(player -> player.getNumRolls() == 0); //ignore players without rolls done
        if(!players.isEmpty()){
            float sumWinRates = 0f;
            for (GameDetails player : players){
                sumWinRates += player.getWinRate();
            }
            return sumWinRates/players.size();
        }
        return 0f;
    }

    public List<? extends RankedDetails> loadPlayersRanked() {

        List<GameDetails> players = loadAllPlayerDetails();
        //players.sort(ranked);
        players.sort(Comparator
                .comparing(GameDetails::getWinRate)
                .thenComparing(GameDetails::getNumRolls)
                .reversed()); //want DESC (higher win rate first + if equals higher num rolls first)
        return players;
    }

    private final Comparator<GameDetails> ranked = (p1, p2) -> {
        //Wanted DESC sorting: first with better win rate.
        // If equals first with more rolls.
        // Default sorting is in order ASC, first with lower value
        //Comparator (p1,2) -> p1 sorted first if function returns <0 (same lògic in standard compare methods)
        int result = -Float.compare(p1.getWinRate(), p2.getWinRate());
        if (result == 0) {
            return -(p1.getNumRolls() - p2.getNumRolls());
        }
        return result;
    };
}
