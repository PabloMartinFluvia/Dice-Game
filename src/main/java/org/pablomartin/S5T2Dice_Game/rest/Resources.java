package org.pablomartin.S5T2Dice_Game.rest;

import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

public interface Resources {

    /**
     * HTTP request: POST /players.
     * Security: any allowed.
     * Goal: register a new client and provide info to access to application's resources.
     * @param credentials optional. If provided must be populated with username AND password.
     * @return on success 201 CREATED. Body: playerId + jwts for authentication.
     */
    ResponseEntity<?> singUp(
            @RequestBody(required = false) @Validated(SetCredentials.class) CredentialsDto credentials);

    /**
     * HTTP request: PUT /players/register-anonymous.
     * Security: authenticated with an Access JWT. Authorized if client has role anonymous.
     * Goal: save a username and password for this anonymous client +  change his role to
     * registered + deny authentication for any old token provided witch has
     * claims of roles/authorities/username.
     * @param credentials must be populated with username AND password.
     * @return on success 200 OK. Body: playerId + new jwt(s) that replace(s) the invalidated one(s).
     */
    ResponseEntity<?> registerAnonymous(
            @RequestBody @Validated(SetCredentials.class) CredentialsDto credentials);

    /**
     * HTTP request: PUT /players.
     * Security: authenticated with an Access JWT. Authorized if client has role registered.
     * Goal: update the username and/or password of this client + deny authentication for
     * any old token provided witch has claim of username (if updated).
     * @param credentials populated with username and/or password.
     * @param principal of the Access JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated client.
     * @return on success 200 OK. Body: playerId + optional (if any token invalidated):
     * new jwt(s) that replace(s) the invalidated one(s).
     */
    ResponseEntity<?> updateCredentials(
            @RequestBody @Validated(UpdateCredentials.class) CredentialsDto credentials,
            @AuthenticationPrincipal Object principal);

    /**
     * HTTP request: POST /login.
     * Security: authenticated with Basic Authentication.
     * Goal: provide new access and refresh jwts.
     * @param principal is an implementation of UserDetails. For identify
     * unequivocally the authenticated client and can be used for obtain the authorities
     * (so it's not needed to load this data from anywhere else).
     * @return on success 200 OK. Body: playerId + jwts for authentication.
     */
    <T extends UserDetails> ResponseEntity<?> login(@AuthenticationPrincipal T principal);

    /**
     * HTTP request: DELETE /logout.
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate the refresh JWT used for authenticate the user. {The access token(s) will expire soon}.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally this refresh token.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> logout(@AuthenticationPrincipal Object principal);

    /**
     * HTTP request: DELETE /logout/all.
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate all the refresh JWT associated to the authenticated user.
     * {The access token(s) will expire soon}.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated user.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> logoutAll(@AuthenticationPrincipal Object principal);

    /**
     * HTTP request: GET /jwts/access.
     * Security: authenticated with a Refresh JWT.
     * Goal: provide a new access JWT for the authenticated client.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated user. Ideally should contain enough info for
     * generate the access JWT.
     * @return on success 200 OK. Body: playerId + access jwt.
     */
    ResponseEntity<?> accessJwt(@AuthenticationPrincipal Object principal);

    /**
     * HTTP request: GET /jwts/reset.
     * Security: authenticated with a Refresh JWT.
     * Goal: invalidate all the refresh JWT associated to the authenticated user.
     * {The access token(s) will expire soon}. + provide new access and refresh jwt.
     * @param principal of the Refresh JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated user. Ideally should contain enough info for
     * generate the access JWT.
     * @return on success 200 OK. Body: playerId + new jwts for authentication.
     */
    ResponseEntity<?> resetJwts(@AuthenticationPrincipal Object principal);
}
