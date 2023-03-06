package org.pablomartin.S5T2Dice_Game.rest;

import jakarta.validation.Valid;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.UUID;

public interface GameResources {

    // ACCESS JWT AUTHENTICATION

    /**
     * HTTP request: GET /players/ranking .
     * Security: authenticated with an Access JWT.
     * Goal: show the average win rate of all players. Players without rolls are ignored.
     * @return on success 200 OK. Body: The average win rate.
     */
    ResponseEntity<?> showAverageWinRate();

    /**
     * HTTP request: GET /players .
     * Security: authenticated with an Access JWT.
     * Goal: provide a list of players sorted by their average win rate (and number of rolls done).
     * @return on success 200 OK. Body: the list of players sorted (username + average + number of rolls).
     */
    ResponseEntity<?> listPlayersRanked();

    /**
     * HTTP request: GET /players/winner .
     * Security: authenticated with an Access JWT.
     * Goal: find the player with better win rate, many if there's a tie.
     * @return on success 200 OK. Body: the player(s) found
     */
    ResponseEntity<?> listBestPlayers();

    /**
     * HTTP request: GET /players/winner .
     * Security: authenticated with an Access JWT.
     * Goal: find the player with worst win rate, many if there's a tie.
     * NOTE: players without num rolls ignored.
     * @return on success 200 OK. Body: the player(s) found
     */
    ResponseEntity<?> listWorstPlayers();

    // ACCESS JWT AUTHENTICATION + ID CLAIMED MATCHES PATH

    /**
     * HTTP request: POST /players/{id}/games .
     * Security: authenticated with an Access JWT. Authorized only if the id requesten in path
     * matches user id (stored inside principal of the authentication).
     * Goal: add the new roll provided linked to the authorized user.
     * @param playerId: value injected from path {id}.
     * @param dto from body request, populated with an array with the values of the dices.
     * @return on success 201 CREATED. Body: the values of the dices + if it's a winner roll or not
     */
    ResponseEntity<?> newRoll(@PathVariable("id") UUID playerId,
                              @RequestBody @Valid RollDto dto);

    /**
     * HTTP request: GET /players/{id}/games .
     * Security: authenticated with an Access JWT. Authorized only if the id requesten in path
     * matches user id (stored inside principal of the authentication).
     * Goal: show all the rolls linked to the authorized user.
     * @param playerId: value injected from path {id}.
     * @return on success 200 OK. Body: all the rolls (with the result) done by the user.
     */
    ResponseEntity<?> listRolls(@PathVariable("id") UUID playerId);

    /**
     * HTTP request: GET /players/{id}/ranking .
     * Security: authenticated with an Access JWT. Authorized only if the id requesten in path
     * matches user id (stored inside principal of the authentication).
     * Goal: show the win rate of the authenticated user.
     * @param playerId: value injected from path {id}.
     * @return on success 200 OK. Body: id + username + win rate + num of rolls done.
     * If the player has no rolls done the win rate is 0%.
     */
    ResponseEntity<?> showWinRate(@PathVariable("id") UUID playerId);

    /**
     * HTTP request: DELETE /players/{id}/games .
     * Security: authenticated with an Access JWT. Authorized only if the id requesten in path
     * matches user id (stored inside principal of the authentication).
     * Goal: remove all the rolls linked to the authorized user.
     * @param playerId: value injected from path {id}.
     * @return on success 204 NO CONTENT.
     */
    ResponseEntity<?> deleteRolls(@PathVariable("id") UUID playerId);
}
