package org.pablomartin.S5T2Dice_Game.rest;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.DetailsJwt;
import org.pablomartin.S5T2Dice_Game.domain.services.Services;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.basic.PlayerDetails;
import org.pablomartin.S5T2Dice_Game.security.jwt.RefreshTokenDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Log4j2
public class AuthenticationsController implements AuthenticationsResources{

    public static final String LOGIN = "/login";

    public static final String LOGOUT = "/logout";

    public static final String LOGOUT_ALL = LOGOUT+"/all";

    private static final String JWTS = "/jwts";

    public static final String JWTS_ACCESS = JWTS+"/access";

    public static final String JWTS_RESET = JWTS+"/reset";

    private final ModelsProvider models;

    private final Services services;

    private final ResponsesProvider responses;


    //BASIC AUTHENTICATION
    @PostMapping(path = LOGIN)
    @Override
    public ResponseEntity<?> login(@AuthenticationPrincipal PlayerDetails principal) {
        DetailsJwt ownerDetails = models.fromBasicPrincipal(principal);
        String[] jwts = services.createJWTS(ownerDetails);
        return responses.forLogin(ownerDetails.getOwnerId(),jwts);
    }

    //REFRESH JWT AUTHENTICATION

    /**
     *
     * Goal: provide a new access JWT for the authenticated client.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated user. Ideally should contain enough info for
     * generate the access JWT.
     * @return on success 200 OK. Body: playerId + access jwt.
     */
    @GetMapping(path = JWTS_ACCESS)
    @Override
    public ResponseEntity<?> accessJwt(@AuthenticationPrincipal RefreshTokenDetails principal) {
        DetailsJwt ownerDetails = models.fromRefreshPrincipal(principal);
        String accessJwt = services.createAccessJWT(ownerDetails);
        return responses.forAccessJwt(ownerDetails.getOwnerId(),accessJwt);
    }

    @GetMapping(path = JWTS_RESET)
    @Override
    public ResponseEntity<?> resetJwts(@AuthenticationPrincipal RefreshTokenDetails principal) {
        DetailsJwt ownerDetails = models.fromRefreshPrincipal(principal);
        //a new service method (instead of callind invalidate all + create new), this allows
        // using @Transactional only in service layer.
        String[] jwts = services.resetTokensFromOwner(ownerDetails);
        return responses.forReset(ownerDetails.getOwnerId(),jwts);
    }

    @DeleteMapping(path = LOGOUT)
    @Override
    public ResponseEntity<?> logout(@AuthenticationPrincipal RefreshTokenDetails principal) {
        UUID refreshTokenId = principal.getRefreshTokenId();
        services.invalidateRefreshToken(refreshTokenId);
        return responses.forLogout();
    }

    @DeleteMapping(path = LOGOUT_ALL)
    @Override
    public ResponseEntity<?> logoutAll(@AuthenticationPrincipal RefreshTokenDetails principal) {
        UUID ownerId = principal.getOwnerId();
        services.invalidateAllRefreshTokensFromOwner(ownerId);
        return responses.forLogoutAll();
    }
}
