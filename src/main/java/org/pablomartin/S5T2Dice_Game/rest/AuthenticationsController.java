package org.pablomartin.S5T2Dice_Game.rest;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AccessDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AuthenticationCredentials;
import org.pablomartin.S5T2Dice_Game.domain.services.AccessService;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.RefreshTokenPrincipal;
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

    private final AccessService service;

    private final ResponsesProvider responses;


    //BASIC AUTHENTICATION
    @PostMapping(path = LOGIN)
    @Override
    public ResponseEntity<?> login(@AuthenticationPrincipal BasicPrincipal principal) {
        AuthenticationCredentials credentials = models.fromBasicPrincipal(principal);
        AccessDetails accessDetails = service.createJWTS(credentials);
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
        AuthenticationCredentials credentials = models.fromRefreshPrincipal(principal);
        AccessDetails accessDetails = service.createAccessJWT(credentials);
        return responses.forAccessJwt(accessDetails);
    }

    @GetMapping(path = JWTS_RESET)
    @Override
    public ResponseEntity<?> resetJwts(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        AuthenticationCredentials credentials = models.fromRefreshPrincipal(principal);
        //a new service method (instead of calling invalidate all + create new), this allows
        // using @Transactional only in service layer.
        AccessDetails accessDetails = service.resetTokensFromOwner(credentials);
        return responses.forReset(accessDetails);
    }

    @DeleteMapping(path = LOGOUT)
    @Override
    public ResponseEntity<?> logout(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        UUID refreshTokenId = principal.getRefreshTokenId();
        service.invalidateRefreshToken(refreshTokenId);
        return responses.forLogout();
    }

    @DeleteMapping(path = LOGOUT_ALL)
    @Override
    public ResponseEntity<?> logoutAll(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        UUID ownerId = principal.getUserId();
        service.invalidateAllRefreshTokensFromOwner(ownerId);
        return responses.forLogoutAll();
    }
}
