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

    protected boolean existsPlayer(@NotNull UUID playerId) {
        return adapter.existsPlayer(playerId);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    public RollDetails saveNewRoll(@NotNull UUID playerId, @NotNull RollDetails roll) {
        assertPlayerExists(playerId);
        roll =adapter.saveRoll(playerId,roll);
        roll.checkResult();
        return roll;
    }

    public List<RollDetails> loadRollsSorted(@NotNull UUID playerId) {
        assertPlayerExists(playerId);
        List<RollDetails> rolls = adapter.findAllRolls(playerId);
        rolls.forEach(RollDetails::checkResult);
        rolls.sort(Comparator.comparing(RollDetails::getInstantRoll));
        return rolls;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    public void deleteRolls(@NotNull UUID playerId) {
        adapter.deleteAllRolls(playerId);
    }

    public RankedDetails loadRanking(@NotNull UUID playerId) {
        GameDetails game = adapter.findGame(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        game.calculateWinRate();
        return game;
    }

    public float loadAverageWinRate() {
        List<GameDetails> games = loadAllGames();
        games.removeIf(game -> game.getNumRolls() == 0); //ignore players without rolls done
        if(!games.isEmpty()){
            float sumWinRates = 0f;
            for (GameDetails game : games){
                sumWinRates += game.getWinRate();
            }
            return sumWinRates/games.size();
        }
        return 0f;
    }

    public List<? extends RankedDetails> loadPlayersRanked() {

        List<GameDetails> games = loadAllGames();
        //players.sort(ranked);
        games.sort(Comparator
                .comparing(GameDetails::getWinRate)
                .thenComparing(GameDetails::getNumRolls)
                .reversed()); //want DESC (higher win rate first + if equals higher num rolls first)
        return games;
    }

    private List<GameDetails> loadAllGames(){
        List<GameDetails> games = adapter.findAllGames();
        games.forEach(GameDetails::calculateWinRate);
        return games;
    }

    public List<RankedDetails> loadTopPlayers() {
        return collectFirsts(loadPlayersRanked());
    }

    private List<RankedDetails> collectFirsts(List<? extends RankedDetails> games){
        float firstWinRate = games.get(0).getWinRate();

        List<RankedDetails> firsts = new LinkedList<>();
        Iterator<? extends RankedDetails> iterator = games.iterator();
        boolean isFirst = true;
        while (isFirst && iterator.hasNext()){
            RankedDetails first = iterator.next();
            if( first.getWinRate() == firstWinRate){
                firsts.add(first);
            }else {
                isFirst = false;
            }
        }
        return firsts;
    }

    public List<RankedDetails> loadWorstPlayers() {
        List<GameDetails> games = loadAllGames();
        games.removeIf(game -> game.getNumRolls() == 0); //ignore players without rolls done
        games.sort(losers);
        return collectFirsts(games);
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

    private final Comparator<GameDetails> losers = (p1, p2) -> {
        //Wanted ASC sorting: first with worst win rate.
        // If equals first with more rolls. (DESC)

        int result = Float.compare(p1.getWinRate(), p2.getWinRate());
        if (result == 0) {
            return -Integer.compare(p1.getNumRolls(), p2.getNumRolls());
        }
        return result;
    };
}
