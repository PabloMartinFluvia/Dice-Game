package org.pablomartin.S5T2Dice_Game.rest;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;
import org.pablomartin.S5T2Dice_Game.domain.services.GameService;
import org.pablomartin.S5T2Dice_Game.rest.documentation.*;
import org.pablomartin.S5T2Dice_Game.rest.documentation.pend.*;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

@RestController
@RequestMapping(produces = "application/json")
@Tag(name = "Game resources.")
@SecurityRequirement(name = "bearer-jwt") //all resources the same
@RequiredArgsConstructor
public class GameController implements GameResources{

    private final ModelsProvider models;

    private final GameService service;

    private final ResponsesProvider responses;

    // ACCESS JWT AUTHENTICATION

    @GetMapping(path = PLAYERS_RANKING)
    @AvgWinRateOperation
    public ResponseEntity<?> showAverageWinRate() {
        float avg = service.loadAverageWinRate();
        return responses.forAverageWinRate(avg);
    }

    @GetMapping(path = PLAYERS)
    @RankingOperation
    public ResponseEntity<?> listPlayersRanked() {
        List<? extends RankedDetails> ranking = service.loadPlayersRanked();
        return responses.forPlayersRanked(ranking);
    }

    // ACCESS JWT AUTHENTICATION + ID CLAIMED MATCHES PATH

    @PostMapping(path = PLAYERS_CONCRETE_ROLLS)
    @NewRollOperation
    public ResponseEntity<?> newRoll(@PathVariable("id") UUID playerId,
                                     @RequestBody @Valid RollDto dto) {
        RollDetails roll = models.fromRoll(dto);
        roll = service.saveNewRoll(playerId,roll);
        return responses.forNewRoll(roll);
    }

    @GetMapping(path = PLAYERS_CONCRETE_ROLLS)
    @AllRollsOperation
    public ResponseEntity<?> listRolls(@PathVariable("id") UUID playerId) {
        List<RollDetails> rollsSorted = service.loadRollsSorted(playerId);
        return responses.forListRolls(rollsSorted);
    }

    @GetMapping(path = PLAYERS_CONCRETE_RANKING)
    @WinRateOperation
    public ResponseEntity<?> showWinRate(@PathVariable("id") UUID playerId) {
        RankedDetails status = service.loadStatus(playerId);
        return responses.forWinRate(status);
    }

    @DeleteMapping(path = PLAYERS_CONCRETE_ROLLS)
    @DeleteRollsOperation
    public ResponseEntity<?> deleteRolls(@PathVariable("id") UUID playerId) {
        service.deleteRolls(playerId);
        return responses.forDeleteRolls();
    }
}
