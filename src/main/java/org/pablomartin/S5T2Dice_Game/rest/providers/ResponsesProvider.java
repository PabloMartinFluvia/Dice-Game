package org.pablomartin.S5T2Dice_Game.rest.providers;

import org.pablomartin.S5T2Dice_Game.domain.models.AccessDetails;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ResponsesProvider {

    //REGISTER

    /**
     * Goal: provide a response for a success singup.
     * @param accessDetails
     * @return 201 CREATED. Body: playerId + username + jwts for authentication.
     */
    ResponseEntity<?> forSingUp(AccessDetails accessDetails);

    /**
     * Goal: provide a response for a success registration of anonymous user.
     * @param accessDetails
     * @return on success 200 OK. Body: playerId + username + access jwt (previous won't be valid anymore).
     */
    ResponseEntity<?> forRegisterAnonymous(AccessDetails accessDetails);

    /**
     * Goal: provide a response for a success update of username and/or password
     * @param accessDetails
     * @return on success 200 OK. Body: playerId + optional if username and/or access jwt are not
     * null in accessDetails: username + access jwt.
     */
    ResponseEntity<?> forUpdateRegistered(AccessDetails accessDetails);

    /**
     * Goal: provide a response on success when user deleted.
     * @return ons success 204 NO CONTENT.
     */
    ResponseEntity<?> fromDeleteUser();

    /**
     * Goal: provide a response on success when user promoted to admin.
     * @return ons success 204 NO CONTENT.
     */
    ResponseEntity<?> fromPromoteUser();

    //AUTHENTICATION

    /**
     * Goal: provide a response for a success login.
     * @param accessDetails
     * @return on success 200 OK. Body: playerId + jwts for authentication.
     */
    ResponseEntity<?> forLogin(AccessDetails accessDetails);

    /**
     * Goal: provide a resonse on success when requesting reset jwts.
     * @param accessDetails
     * @return on success 200 OK. Body: playerId + new jwts for authentication.
     */
    ResponseEntity<?> forReset(AccessDetails accessDetails);

    /**
     * Goal: provide a response on success when requesting a new access jwt.
     * @param accessDetails
     * @return on success 200 OK. Body: playerId + access jwt for authentication.
     */
    ResponseEntity<?> forAccessJwt(AccessDetails accessDetails);


    /**
     * Goal: provide a response for a success logout.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> forLogout();

    /**
     * Goal: provide a response for a success logout-all.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> forLogoutAll();

}
