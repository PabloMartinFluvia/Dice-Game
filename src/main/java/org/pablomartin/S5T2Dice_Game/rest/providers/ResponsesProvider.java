package org.pablomartin.S5T2Dice_Game.rest.providers;

import org.springframework.http.ResponseEntity;

import java.util.UUID;

public interface ResponsesProvider {

    /**
     * Goal: provide a response for a success login.
     * @param playerId
     * @param jwts
     * @return on success 200 OK. Body: playerId + jwts for authentication.
     */
    ResponseEntity<?> forLogin(UUID playerId, String[] jwts);

    /**
     * Goal: provide a resonse on success when requesting reset jwts.
     * @param playerId
     * @param jwts
     * @return on success 200 OK. Body: playerId + new jwts for authentication.
     */
    ResponseEntity<?> forReset(UUID playerId, String[] jwts);

    /**
     * Goal: provide a response on success when requesting a new access jwt.
     * @param playerId
     * @param accessJwt
     * @return on success 200 OK. Body: playerId + access jwt for authentication.
     */
    ResponseEntity<?> forAccessJwt(UUID playerId, String accessJwt);


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
