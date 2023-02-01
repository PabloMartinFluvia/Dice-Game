package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.GamePersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.game.PlayerDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.StatusDetails;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DefaultGameService implements GameService{

    private  final GamePersistenceAdapter adapter;

    @Override
    public RollDetails saveNewRoll(@NotNull UUID targetPlayerId, @NotNull RollDetails roll) {
        roll =adapter.saveRoll(targetPlayerId,roll);
        roll.updateIfWon();
        return roll;
    }

    @Override
    public Collection<RollDetails> loadRolls(@NotNull UUID playerId) {
        Collection<RollDetails> rolls = adapter.findAllRolls(playerId);
        rolls.forEach(RollDetails::updateIfWon);
        return Collections.unmodifiableCollection(rolls);
    }

    @Override
    public void deleteRolls(@NotNull UUID playerId) {
        adapter.deleteAllRolls(playerId);
    }

    @Override
    public StatusDetails loadStatus(@NotNull UUID playerId) {
        PlayerDetails player = adapter.findPlayer(playerId);
        updatePlayerDetails(player);
        return player;
    }

    private void updatePlayerDetails(@NotNull PlayerDetails player){
        player.updateRollsDetails();
        player.calculateWinRate();
    }

    @Override
    public Collection<StatusDetails> loadPlayersRanked() {
        List<PlayerDetails> players = loadPlayerDetails();
        players.sort(playerComparator());
        return Collections.unmodifiableCollection(players);
    }

    private List<PlayerDetails> loadPlayerDetails(){
        List<PlayerDetails> players = adapter.findAllPlayers();
        players.forEach(this::updatePlayerDetails);
        return players;
    }
    private Comparator<PlayerDetails> playerComparator(){
        return (p1,p2) -> {
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

    @Override
    public float loadAverageWinRate() {
        List<PlayerDetails> players = loadPlayerDetails();
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











}
