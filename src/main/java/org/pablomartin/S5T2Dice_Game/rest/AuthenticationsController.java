package org.pablomartin.S5T2Dice_Game.rest;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AccessDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.JwtCredentialsProvider;
import org.pablomartin.S5T2Dice_Game.domain.services.Services;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.basic.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.jwt.RefreshTokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class AuthenticationsController implements AuthenticationsResources{

    private final ModelsProvider models;

    private final Services services;

    private final ResponsesProvider responses;


    //BASIC AUTHENTICATION
    @PostMapping(path = LOGIN)
    @Override
    public ResponseEntity<?> login(@AuthenticationPrincipal BasicPrincipal principal) {
        JwtCredentialsProvider ownerDetails = models.fromBasicPrincipal(principal);
        AccessDetails accessDetails = services.createJWTS(ownerDetails);
        return responses.forLogin(accessDetails);
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
    public ResponseEntity<?> accessJwt(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        JwtCredentialsProvider ownerDetails = models.fromRefreshPrincipal(principal);
        AccessDetails accessDetails = services.createAccessJWT(ownerDetails);
        return responses.forAccessJwt(accessDetails);
    }

    @GetMapping(path = JWTS_RESET)
    @Override
    public ResponseEntity<?> resetJwts(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        JwtCredentialsProvider ownerDetails = models.fromRefreshPrincipal(principal);
        //a new service method (instead of callind invalidate all + create new), this allows
        // using @Transactional only in service layer.
        AccessDetails accessDetails = services.resetTokensFromOwner(ownerDetails);
        return responses.forReset(accessDetails);
    }

    @DeleteMapping(path = LOGOUT)
    @Override
    public ResponseEntity<?> logout(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        UUID refreshTokenId = principal.getRefreshTokenId();
        services.invalidateRefreshToken(refreshTokenId);
        return responses.forLogout();
    }

    @DeleteMapping(path = LOGOUT_ALL)
    @Override
    public ResponseEntity<?> logoutAll(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        UUID ownerId = principal.getOwnerId();
        services.invalidateAllRefreshTokensFromOwner(ownerId);
        return responses.forLogoutAll();
    }
}
