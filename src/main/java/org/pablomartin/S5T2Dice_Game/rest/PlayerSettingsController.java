package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.AuthenticationService;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
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

    private final RequestResponseInterpreter comunicationsManager;

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
    public ResponseEntity<?>  singUp( @RequestBody(required = false) //if not provided -> ANONIM player
                @Validated(value = SetCredentials.class) CredentialsDto credentialsDto){ //full credentials validations
        Player player = comunicationsManager.parseCredentials(credentialsDto);
        Token refreshToken = authenticationService.performSingup(player);
        String[] jwts = jwtService.generateJwts(refreshToken);
        return comunicationsManager.singupResponse(refreshToken.getOwner(),jwts);
    }

    /*
    Security: allowed only annonimus role
    Requires a SingupDto full qualified
    On success User's role changes -> access jwt no longer valid
        *refresh tokens still valid
    Access info provided: id + new username + new access token
     */
    @PutMapping(path = "/credentials")
    public ResponseEntity<?> registerAnonymous(
            @RequestBody @Validated(value = SetCredentials.class) CredentialsDto credentialsDto, //full credentials validations
            @AuthenticationPrincipal UUID playerId){
        Player player = updateCredentials(credentialsDto, playerId);
        String accessJwt = jwtService.generateAccessJwt(player);
        return comunicationsManager.accessJwtResponse(player,accessJwt);
    }

    /*
    Security: registered role
    Requires a SingupDto: only fields not null will be updated
    Provided jwts continue being valid
    Access info provided: id + new username (if updated)
     */
    @PutMapping
    public ResponseEntity<?> updateRegistered(@RequestBody @Valid CredentialsDto credentialsDto,
                                                    //default group validation
                                                    @AuthenticationPrincipal UUID playerId){
        Player player = updateCredentials(credentialsDto, playerId);
        return comunicationsManager.usernameResponse(player);
    }

    private Player updateCredentials(CredentialsDto dto, UUID id){
        Player credentialsProvider = comunicationsManager.parseCredentials(dto);
        credentialsProvider.setPlayerId(id);
        return authenticationService.uptadeBasicCredentials(credentialsProvider);
    }
}
