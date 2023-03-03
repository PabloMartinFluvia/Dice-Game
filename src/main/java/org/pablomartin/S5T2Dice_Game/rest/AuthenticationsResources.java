package org.pablomartin.S5T2Dice_Game.rest;

import org.pablomartin.S5T2Dice_Game.security.basic.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.RefreshTokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

public interface AuthenticationsResources extends SecuredResource{

    //BASIC AUTHENTICATION:

    /**
     * HTTP request: POST /login .
     * Security: authenticated with Basic Authentication.
     * Goal: provide new access and refresh jwts.
     * @return on success 200 OK. Body: playerId + jwts for authentication.
     */
    ResponseEntity<?> login(); //secured

    //REFRESH JWT AUTHENTICATION:

    /**
     * HTTP request: GET /jwts/access .
     * Security: authenticated with a Refresh JWT.
     * Goal: provide a new access JWT for the authenticated client.
     * @return on success 200 OK. Body: playerId + access jwt.
     */
    ResponseEntity<?> accessJwt();

    /**
     * HTTP request: GET /jwts/reset .
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate all the refresh JWT associated to the authenticated user.
     * {The access token(s) will expire soon}. + provide new access and refresh jwt.
     * @return on success 200 OK. Body: playerId + new jwts for authentication.
     */
    ResponseEntity<?> resetJwts();

    /**
     * HTTP request: DELETE /logout .
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate the refresh JWT used for authenticate the user. {The access token(s) will expire soon}.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> logout();

    /**
     * HTTP request: DELETE /logout/all .
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate all the refresh JWT associated to the authenticated user.
     * {The access token(s) will expire soon}.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> logoutAll();
}
