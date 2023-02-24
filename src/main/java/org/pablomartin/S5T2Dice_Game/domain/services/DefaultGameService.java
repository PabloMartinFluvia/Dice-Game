package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.GamePersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.game.PlayerDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.StatusDetails;
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
        roll.updateIfWon();
        return roll;
    }

    @Override
    public Collection<RollDetails> loadRolls(@NotNull UUID playerId) {
        assertPlayerExists(playerId);
        List<RollDetails> rolls = adapter.findAllRolls(playerId);
        rolls.forEach(RollDetails::updateIfWon);
        rolls.sort(Comparator.comparing(RollDetails::getInstantRoll));
        return Collections.unmodifiableCollection(rolls);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void deleteRolls(@NotNull UUID playerId) {
        adapter.deleteAllRolls(playerId);
    }

    @Override
    public StatusDetails loadStatus(@NotNull UUID playerId) {
        PlayerDetails player = adapter.findPlayer(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        updatePlayerDetails(player);
        return player;
    }

    private void updatePlayerDetails(@NotNull PlayerDetails player){
        player.updateRollsDetails();
        player.calculateWinRate();
    }

    private List<PlayerDetails> loadAllPlayerDetails(){
        List<PlayerDetails> players = adapter.findAllPlayers();
        players.forEach(this::updatePlayerDetails);
        return players;
    }

    @Override
    public float loadAverageWinRate() {
        List<PlayerDetails> players = loadAllPlayerDetails();
        players.removeIf(player -> player.getNumRolls() == 0);
        if(!players.isEmpty()){
            float sumWinRates = 0f;
            for (PlayerDetails player : players){
                sumWinRates += player.getWinRate();
            }
            return sumWinRates/players.size();
        }
        return 0f;
    }

    @Override
    public Collection<StatusDetails> loadPlayersRanked() {
        List<PlayerDetails> players = loadAllPlayerDetails();
        //players.sort(ranked);
        players.sort(Comparator
                .comparing(PlayerDetails::getWinRate)
                .thenComparing(PlayerDetails::getNumRolls)
                .reversed()); //want DESC (higher win rate first + if equals higher num rolls first)
        return Collections.unmodifiableCollection(players);
    }

    private Comparator<PlayerDetails> ranked = (p1,p2) -> {
        //Wanted DESC sorting: first with better winrate.
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
