package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;
import org.pablomartin.S5T2Dice_Game.domain.services.GameService;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class GameController implements GameResources{

    private final ModelsProvider models;

    private final GameService service;

    private final ResponsesProvider responses;

    // ACCESS JWT AUTHENTICATION

    @GetMapping(path = PLAYERS_RANKING)
    @Override
    public ResponseEntity<?> showAverageWinRate() {
        float avg = service.loadAverageWinRate();
        return responses.forAverageWinRate(avg);
    }

    @GetMapping(path = PLAYERS)
    @Override
    public ResponseEntity<?> listPlayersRanked() {
        Collection<RankedDetails> ranking = service.loadPlayersRanked();
        return responses.forPlayersRanked(ranking);
    }

    // ACCESS JWT AUTHENTICATION + ID CLAIMED MATCHES PATH

    @PostMapping(path = PLAYERS_CONCRETE_ROLLS)
    @Override
    public ResponseEntity<?> newRoll(@PathVariable("id") UUID playerId,
                                     @RequestBody @Valid RollDto dto) {
        RollDetails roll = models.fromRoll(dto);
        roll = service.saveNewRoll(playerId,roll);
        return responses.forNewRoll(roll);
    }

    @GetMapping(path = PLAYERS_CONCRETE_ROLLS)
    @Override
    public ResponseEntity<?> listRolls(@PathVariable("id") UUID playerId) {
        Collection<RollDetails> rolls = service.loadRolls(playerId);
        return responses.forListRolls(rolls);
    }

    @GetMapping(path = PLAYERS_CONCRETE_RANKING)
    @Override
    public ResponseEntity<?> showWinRate(@PathVariable("id") UUID playerId) {
        RankedDetails status = service.loadStatus(playerId);
        return responses.forWinRate(status);
    }

    @DeleteMapping(path = PLAYERS_CONCRETE_ROLLS)
    @Override
    public ResponseEntity<?> deleteRolls(@PathVariable("id") UUID playerId) {
        service.deleteRolls(playerId);
        return responses.forDeleteRolls();
    }
}
