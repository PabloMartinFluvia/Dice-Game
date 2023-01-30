package org.pablomartin.S5T2Dice_Game.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/players")
@RequiredArgsConstructor
@Log4j2
public class GameController implements GameResources{

    @Override
    public ResponseEntity<?> showAverageWinRate() {
        return null;
    }

    @Override
    public ResponseEntity<?> listRanked() {
        return null;
    }

    @Override
    public ResponseEntity<?> addRoll(UUID playerId, RollDto roll) {
        return null;
    }

    @Override
    public ResponseEntity<?> listRolls(UUID playerId) {
        return null;
    }

    @Override
    public ResponseEntity<?> showWinRate(UUID playerId) {
        return null;
    }

    @Override
    public ResponseEntity<?> deleteRolls(UUID playerId) {
        return null;
    }

    /*
    private final RequestResponseInterpreter comunicationsManager;

    private final PlayersService playersService;


    //secured: path varibale must match with the id of the user authenticated

    @PostMapping(path = "/{id}/games")
    //@PreAuthorize("#playerId == principal") // works, reads the argument value and evaluates
    public ResponseEntity<?> addRoll(@RequestBody @Valid RollDto rollDto,
                                           @PathVariable("id") UUID playerId){
                                           //@AuthenticationPrincipal UUID playerId){
        Roll roll = comunicationsManager.parseRollDto(rollDto);



        log.info("--------"+playerId.toString());
        //Roll roll = gameService.addRoll(id,nums);
        //return response.forRoll(roll);
        return ResponseEntity.ok().build();
    }

    */
}
