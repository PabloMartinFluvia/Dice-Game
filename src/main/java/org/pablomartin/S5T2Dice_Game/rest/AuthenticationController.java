package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.AuthenticationService;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.domain.services.PlayersService;
import org.pablomartin.S5T2Dice_Game.rest.interpreters.RequestResponseInterpreter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.SingupDto;
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

    private final PlayersService playersService;


    /*
    Basic Auth -> Credentials in header
    Mehot's main goal -> authenticate the client and provide tokens (access and refresh)
    GET:
        PROS: 200 ok response + coherent with method's goal (provide the tokens), even it's not a "login resource".
        CONS: this endpoint it's not idempotent (refresh token persisted)
    POST:
        CONS: response should be 201. + Won't be a new "login resource" persisted
        PROS: more secure + adaptable to web form + endpoint no idempotent
    None are 100% perfect.
     */
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

    /*
    Method's goal: invalidate provided credentials for authentication -> NO idempotent
        *Invalidates the refresh token. The access token will expire soon
    Status on success: 200 ok or 204 no content
    -> DELETE seems better for a REST API
     */
    @DeleteMapping(path = "/logout")
    public ResponseEntity<?> logout(@AuthenticationPrincipal Token token){
        authenticationService.invalidateRefreshToken(token.getTokenId());
        return interpreter.noContentResponse();
    }

    /*
    Method's goal: invalidate all credentials for this client -> NO idempotent
        *Invalidates all the refresh token stored. The access tokens will expire soon
    Status on success: 200 ok or 204 no content
    -> DELETE seems better for a REST API
     */
    @DeleteMapping(path = "/logout/all")
    public ResponseEntity<?> logoutAll(@AuthenticationPrincipal Token token){
        authenticationService.invalidateAllRefreshToken(token.getOwner());
        return interpreter.noContentResponse();
    }

    /*
    provide new access token when authenticated with refresh token
     */
    @GetMapping(path = "/jwts/access")
    public ResponseEntity<?> newAccessJwt (@AuthenticationPrincipal Token token){
        //Authentication from Refresh JWT has not enought info to generate and acces JWT
        Player player = playersService.findPlayerById(token.getOwner().getPlayerId());
        String accessJwt = jwtService.generateAccessJwt(player);
        return interpreter.accessJwtResponse(player,accessJwt);
    }

    /*
    Invalidates all refresh tokens (access tokens will expire soon)
    +
     provides new access jwt and refresh jwt
     */
    @GetMapping(path = "/jwts/reset")
    public ResponseEntity<?> resetJwts (@AuthenticationPrincipal Token token){
        Player player = playersService.findPlayerById(token.getOwner().getPlayerId());
        Token refreshToken = authenticationService.performReset(player);
        String[] jwts = jwtService.generateJwts(refreshToken);
        return interpreter.resetJwtResponse(jwts);
    }
}
