package org.pablomartin.S5T2Dice_Game.rest;

import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.TokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface SettingsResources extends SecuredResource{

    //ANY ALLOWED

    /**
     * HTTP request: POST /players .
     * Security: any allowed.
     * Goal: register a new client and provide info to access to application's resources.
     * @param dto optional, from body request. If provided must be populated with username AND password.
     * @return on success 201 CREATED. Body: playerId + username + jwts for authentication.
     */
    ResponseEntity<?> singUp(
            @RequestBody(required = false) @Validated(SetCredentials.class) CredentialsDto dto);

    // ACCESS JWT AUTHENTICATION + ROLE ANONYMOUS

    /**
     * HTTP request: PUT /players/register-anonymous .
     * Security: authenticated with an Access JWT. Authorized if client has role anonymous.
     * Goal: save a username and password for this anonymous client +  change his role to
     * registered + deny authentication for any old token provided witch has
     * claims of roles/authorities/username.
     * @param dto from body request, must be populated with username AND password.
     * @return on success 200 OK. Body: playerId + username + access jwt (previous won't be valid anymore).
     */
    ResponseEntity<?> registerAnonymous(
            @RequestBody @Validated(SetCredentials.class) CredentialsDto dto);

    // ACCESS JWT AUTHENTICATION + ROLE REGISTERED

    /**
     * HTTP request: PUT /players .
     * Security: authenticated with an Access JWT. Authorized if client has role registered.
     * Goal: update the username and/or password of this client + deny authentication for
     * any old token provided witch has claim of username (if updated).
     * @param dto from body request, populated with username and/or password.
     * @return on success 200 OK. Body: playerId + optional if username changes: username + access jwt.
     */
    ResponseEntity<?> updateRegistered(
            @RequestBody @Validated(UpdateCredentials.class) CredentialsDto dto);

    // ACCESS JWT AUTHENTICATION + ROLE ADMIN

    /**
     * HTTP request: DELETE admins/players/{id} .
     * Security: authenticated with an Access JWT. Authorized if user has role Admin.
     * Goal: Remove all data related to the user (only if it's not admin) specified in the path.
     * @param targetNotAdminUserId value injected from path {id}. The id of the target player.
     * @return ons success 204 NO CONTENT.
     */
    ResponseEntity<?> deleteUser(@PathVariable("id") UUID targetNotAdminUserId);

}
