package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.AuthenticationService;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.rest.interpreters.RequestResponseInterpreter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.LoginDto;
import org.pablomartin.S5T2Dice_Game.security.basic.PlayerDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class AuthenticationController {

    private final RequestResponseInterpreter interpreter;

    private final AuthenticationService authenticationService;

    private final JwtService jwtService;

    @PostMapping(path = "/players")
    public ResponseEntity<?>  singup(@RequestBody(required = false) @Valid LoginDto loginDto){
        Player player = interpreter.toPlayer(loginDto);
        Token refreshToken = authenticationService.performNewSingup(player);
        String[] jwts = jwtService.generateJwts(refreshToken);
        return interpreter.singupResponse(refreshToken.getOwner(),jwts);
    }

    @PostMapping(path = "/login")
    public ResponseEntity<?> login (@AuthenticationPrincipal PlayerDetails playerDetails){
        Token refreshToken = authenticationService.performLogin(playerDetails.getUsername());
        String[] jwts = jwtService.generateJwts(refreshToken);
        return interpreter.loginResponse(refreshToken.getOwner(),jwts);
    }

    /*
    Following resources:
    Authenticated with a refresh jwt -> Authentication's principal is instance of Token.class
     */
    @PostMapping(path = "/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Token token){
        log.info("---------"+token.getTokenId());
        log.info("---------"+token.getOwner().getPlayerId());
        return interpreter.okResponse();
    }


}
