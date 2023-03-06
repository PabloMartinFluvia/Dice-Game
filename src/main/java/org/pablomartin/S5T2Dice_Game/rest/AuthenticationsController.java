/*
    @AuthenticationPrincipal as an argument:
    If type is a concrete class there's no problem (ex: DefaultPrincipal.class).
    In this project I want to work with interfaces, the more, the better (even with models).
    In theory shouldn't be any problem, they say it works with UserDetails and OAuth2User (interfaces).
    And it's true, /login with @AuthenticationPrincipal UserDetails principal works,
    BUT if I change to @AuthenticationPrincipal BasicPrincipal principal NOT WORKS
        *Note: BasicPrincipal extends UserDetails
        Argument it's loaded as an empty proxy.
        Issue (unresolved) disscussed here:
        https://github.com/spring-projects/spring-security/issues/10930
     */

package org.pablomartin.S5T2Dice_Game.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.AccessInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.services.AccessService;
import org.pablomartin.S5T2Dice_Game.rest.documentation.*;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.basic.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.jwt.providers.RefreshTokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

@RestController
@RequestMapping(produces = "application/json")
@Tag(name = "JWT resources.")
@RequiredArgsConstructor
public class AuthenticationsController implements AuthenticationsResources{

    private final ModelsProvider models;

    private final AccessService service;

    private final ResponsesProvider responses;


    //BASIC AUTHENTICATION
    @PostMapping(path = LOGIN)
    @LoginOperation
    public ResponseEntity<?> login() {
        BasicPrincipal principal = loadPrincipal(BasicPrincipal.class);

        SecurityClaims credentials = models.fromBasicPrincipal(principal);
        AccessInfo accessInfo = service.createJWTS(credentials);
        return responses.forLogin(accessInfo);
    }

    //REFRESH JWT AUTHENTICATION

    @GetMapping(path = JWTS_ACCESS)
    @AccessOperation
    public ResponseEntity<?> accessJwt() {
        RefreshTokenPrincipal principal = loadPrincipal(RefreshTokenPrincipal.class);

        SecurityClaims credentials = models.fromRefreshPrincipal(principal);
        AccessInfo accessInfo = service.createAccessJWT(credentials);
        return responses.forAccessJwt(accessInfo);
    }

    @GetMapping(path = JWTS_RESET)
    @ResetOperation
    public ResponseEntity<?> resetJwts() {
        RefreshTokenPrincipal principal = loadPrincipal(RefreshTokenPrincipal.class);

        SecurityClaims credentials = models.fromRefreshPrincipal(principal);
        AccessInfo accessInfo = service.resetTokensFromOwner(credentials);
        return responses.forReset(accessInfo);
    }

    @DeleteMapping(path = LOGOUT)
    @LogoutOperation
    public ResponseEntity<?> logout() {
        UUID refreshTokenId = loadPrincipal(RefreshTokenPrincipal.class).getRefreshTokenId();

        service.invalidateRefreshToken(refreshTokenId);
        return responses.forLogout();
    }

    @DeleteMapping(path = LOGOUT_ALL)
    @LogoutAllOperation
    public ResponseEntity<?> logoutAll() {
        UUID ownerId = loadPrincipal(RefreshTokenPrincipal.class).getUserId();

        service.invalidateAllRefreshTokensFromOwner(ownerId);
        return responses.forLogoutAll();
    }
}
