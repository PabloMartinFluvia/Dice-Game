package org.pablomartin.S5T2Dice_Game.rest;

import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface SettingsResoruces {

    //ANY ALLOWED

    /**
     * HTTP request: POST /players .
     * Security: any allowed.
     * Goal: register a new client and provide info to access to application's resources.
     * @param credentials optional, from body request. If provided must be populated with username AND password.
     * @return on success 201 CREATED. Body: playerId + jwts for authentication.
     */
    ResponseEntity<?> singUp(
            @RequestBody(required = false) @Validated(SetCredentials.class) CredentialsDto credentials);

    // ACCESS JWT AUTHENTICATION + ROLE ANONYMOUS

    /**
     * HTTP request: PUT /players/register-anonymous .
     * Security: authenticated with an Access JWT. Authorized if client has role anonymous.
     * Goal: save a username and password for this anonymous client +  change his role to
     * registered + deny authentication for any old token provided witch has
     * claims of roles/authorities/username.
     * @param credentials from body request, must be populated with username AND password.
     * @return on success 200 OK. Body: playerId + new jwt(s) that replace(s) the invalidated one(s).
     */
    ResponseEntity<?> registerAnonymous(
            @RequestBody @Validated(SetCredentials.class) CredentialsDto credentials);

    // ACCESS JWT AUTHENTICATION + ROLE REGISTERED

    /**
     * HTTP request: PUT /players .
     * Security: authenticated with an Access JWT. Authorized if client has role registered.
     * Goal: update the username and/or password of this client + deny authentication for
     * any old token provided witch has claim of username (if updated).
     * @param credentials from body request, populated with username and/or password.
     * @param principal of the Access JWT's Authentication. Must contain enough data for
     * identify unequivocally the authenticated client.
     * @return on success 200 OK. Body: playerId + optional (if any token invalidated):
     * new jwt(s) that replace(s) the invalidated one(s).
     */
    ResponseEntity<?> updateRegistered(
            @RequestBody @Validated(UpdateCredentials.class) CredentialsDto credentials,
            @AuthenticationPrincipal Object principal);

    // ACCESS JWT AUTHENTICATION + ROLE ADMIN

    /**
     * HTTP request: DELETE /players/{id} .
     * Security: authenticated with an Access JWT. Authorized if has role Admin.
     * Goal: Remove all data related to the user specified in the path.
     * @param playerId value injected from path {id}. The id of the target player.
     * @return ons success 204 NO CONTENT.
     */
    ResponseEntity<?> delete(@PathVariable("id") UUID playerId);

    /**
     * HTTP request: PUT players/{id}/admin .
     * Security: authenticated with an Access JWT. Authorized if has role Admin.
     * Goal: Give to the specified user in path (must be REGISTERED) all the
     * authorities related to an ADMIN role.
     * @param playerId value injected from path {id}. The id of the target player.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> promote(@PathVariable("id") UUID playerId);
}
