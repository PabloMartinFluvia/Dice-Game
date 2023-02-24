package org.pablomartin.S5T2Dice_Game.rest;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.InfoForAppAccess;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.services.AccessService;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.old.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.old.RefreshTokenPrincipal;
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
        SecurityClaims credentials = models.fromBasicPrincipal(principal);
        InfoForAppAccess infoForAppAccess = service.createJWTS(credentials);
        return responses.forLogin(infoForAppAccess);
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
        SecurityClaims credentials = models.fromRefreshPrincipal(principal);
        InfoForAppAccess infoForAppAccess = service.createAccessJWT(credentials);
        return responses.forAccessJwt(infoForAppAccess);
    }

    @GetMapping(path = JWTS_RESET)
    @Override
    public ResponseEntity<?> resetJwts(@AuthenticationPrincipal RefreshTokenPrincipal principal) {
        SecurityClaims credentials = models.fromRefreshPrincipal(principal);
        //a new service method (instead of calling invalidate all + create new), this allows
        // using @Transactional only in service layer.
        InfoForAppAccess infoForAppAccess = service.resetTokensFromOwner(credentials);
        return responses.forReset(infoForAppAccess);
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
