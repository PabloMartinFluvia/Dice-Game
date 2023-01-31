package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.StatusDetails;
import org.pablomartin.S5T2Dice_Game.domain.services.Services;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class GameController implements GameResources{

    public static final String PLAYERS = "/players";

    public static final String RANKING = "/ranking";

    private static final String PLAYERS_CONCRETE = PLAYERS+"/{id}";

    public static final String PLAYERS_CONCRETE_ROLLS = PLAYERS_CONCRETE +"/games";

    public static final String PLAYERS_CONCRETE_RANKING = PLAYERS_CONCRETE+RANKING;

    private static final String PLAYERS_RANKING = PLAYERS+RANKING;

    private final ModelsProvider models;

    private final Services services;

    private final ResponsesProvider responses;

    // ACCESS JWT AUTHENTICATION

    @GetMapping(path = PLAYERS_RANKING)
    @Override
    public ResponseEntity<?> showAverageWinRate() {
        float avg = services.loadAverageWinRate();
        return responses.forAverageWinRate(avg);
    }

    @GetMapping(path = PLAYERS)
    @Override
    public ResponseEntity<?> listPlayersRanked() {
        LinkedHashSet<StatusDetails> ranking = services.loadPlayersRanked();
        return responses.forPlayersRanked(ranking);
    }

    // ACCESS JWT AUTHENTICATION + ID CLAIMED MATCHES PATH

    @PostMapping(path = PLAYERS_CONCRETE_ROLLS)
    @Override
    public ResponseEntity<?> newRoll(@PathVariable("id") UUID playerId,
                                     @RequestBody @Valid RollDto dto) {
        RollDetails roll = models.fromRoll(dto);
        roll = services.saveNewRoll(playerId,roll);
        return responses.forNewRoll(roll);
    }

    @GetMapping(path = PLAYERS_CONCRETE_ROLLS)
    @Override
    public ResponseEntity<?> listRolls(@PathVariable("id") UUID playerId) {
        Collection<RollDetails> rolls = services.loadRolls(playerId);
        return responses.forListRolls(rolls);
    }

    @GetMapping(path = PLAYERS_CONCRETE_RANKING)
    @Override
    public ResponseEntity<?> showWinRate(@PathVariable("id") UUID playerId) {
        StatusDetails status = services.loadWinRate(playerId);
        return responses.forWinRate(status);
    }

    @DeleteMapping(path = PLAYERS_CONCRETE_ROLLS)
    @Override
    public ResponseEntity<?> deleteRolls(@PathVariable("id") UUID playerId) {
        services.deleteRolls(playerId);
        return responses.forDeleteRolls();
    }
}
