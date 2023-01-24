package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.AuthenticationService;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.rest.dtos.SingupDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.FullPopulated;
import org.pablomartin.S5T2Dice_Game.rest.interpreters.RequestResponseInterpreter;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(path = "/players")
@RequiredArgsConstructor
@Log4j2
public class PlayerSettingsController {

    private final RequestResponseInterpreter interpreter;

    private final AuthenticationService authenticationService;

    private final JwtService jwtService;

    /*
    Unsecured.
    POST method: persisting new data to resource. 201 response on success.
        Getting tokens in response are not the main method's goal
    If it's provided a SingupDto -> it must be full populated
    Access info provided: id + username + access jwt + refresh jwt
     */
    @PostMapping
    public ResponseEntity<?>  singup(
               @RequestBody(required = false) @Validated(value = FullPopulated.class) SingupDto singupDto){ //full credentials validations
        Player player = interpreter.toPlayer(singupDto);
        Token refreshToken = authenticationService.performNewSingup(player);
        String[] jwts = jwtService.generateJwts(refreshToken);
        return interpreter.singupResponse(refreshToken.getOwner(),jwts);
    }

    /*
    Security: registered role
    Requires a SingupDto: only fields not null will be updated
    Provided jwts continue being valid
    Access info provided: id + new username (if updated)
     */
    @PutMapping
    public ResponseEntity<?> updateBasicCredentials(@RequestBody @Valid SingupDto singupDto){ //default group validation
        Player source = interpreter.toPlayer(singupDto);



        log.info(singupDto.getUsername());
        log.info(singupDto.getPassword());
        return  ResponseEntity.ok().build();
    }

    /*
    Security: allowed only annonimus role
    Requires a SingupDto full qualified
    On success User's role changes -> access jwt no longer valid
    Access info provided: id + new username + new access token
     */
    @PostMapping(path = "/credentials")
    public ResponseEntity<?> registerBasicCredentials(
            @RequestBody @Validated(value = FullPopulated.class) SingupDto singupDto, //full credentials validations
            @AuthenticationPrincipal UUID playerId){
        log.info(singupDto.getUsername());
        log.info(singupDto.getPassword());
        log.info(playerId.toString());
        return  ResponseEntity.ok().build();
    }
}
