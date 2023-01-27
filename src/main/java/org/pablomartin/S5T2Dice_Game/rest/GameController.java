package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.Roll;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.PlayersService;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.rest.interpreters.RequestResponseInterpreter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/players")
@RequiredArgsConstructor
@Log4j2
public class GameController {

    private final RequestResponseInterpreter comunicationsManager;

    private final PlayersService playersService;

    /*
    secured: path varibale must match with the id of the user authenticated
     */
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


}
