package org.pablomartin.S5T2Dice_Game.rest;

import org.pablomartin.S5T2Dice_Game.security.basic.PlayerPrincipalDetails;
import org.pablomartin.S5T2Dice_Game.security.jwt.RefreshTokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface AuthenticationsResources {

    //BASIC AUTHENTICATION:

    /**
     * HTTP request: POST /login .
     * Security: authenticated with Basic Authentication.
     * Goal: provide new access and refresh jwts.
     * @param principal is an implementation of UserDetails. For identify
     * unequivocally the authenticated client and can be used for obtain the authorities
     * (so it's not needed to load this data from anywhere else).
     * @return on success 200 OK. Body: playerId + jwts for authentication.
     */
    ResponseEntity<?> login(@AuthenticationPrincipal PlayerPrincipalDetails principal);

    //REFRESH JWT AUTHENTICATION:

    /**
     * HTTP request: GET /jwts/access .
     * Security: authenticated with a Refresh JWT.
     * Goal: provide a new access JWT for the authenticated client.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated user. Ideally should contain enough info for
     * generate the access JWT.
     * @return on success 200 OK. Body: playerId + access jwt.
     */
    ResponseEntity<?> accessJwt(@AuthenticationPrincipal RefreshTokenPrincipal principal);

    /**
     * HTTP request: GET /jwts/reset .
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate all the refresh JWT associated to the authenticated user.
     * {The access token(s) will expire soon}. + provide new access and refresh jwt.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated user. Ideally should contain enough info for
     * generate the access JWT.
     * @return on success 200 OK. Body: playerId + new jwts for authentication.
     */
    ResponseEntity<?> resetJwts(@AuthenticationPrincipal RefreshTokenPrincipal principal);

    /**
     * HTTP request: DELETE /logout .
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate the refresh JWT used for authenticate the user. {The access token(s) will expire soon}.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally this refresh token.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> logout(@AuthenticationPrincipal RefreshTokenPrincipal principal);

    /**
     * HTTP request: DELETE /logout/all .
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate all the refresh JWT associated to the authenticated user.
     * {The access token(s) will expire soon}.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated user.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> logoutAll(@AuthenticationPrincipal RefreshTokenPrincipal principal);
}
