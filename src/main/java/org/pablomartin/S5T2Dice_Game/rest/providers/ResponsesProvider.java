package org.pablomartin.S5T2Dice_Game.rest.providers;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.InfoForAppAccess;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;
import org.springframework.http.ResponseEntity;

import java.util.Collection;

public interface ResponsesProvider {

    //REGISTER

    /**
     * Goal: provide a response for a success singup.
     * @param details
     * @return 201 CREATED. Body: playerId + username + jwts for authentication.
     */
    ResponseEntity<?> forSingUp(@NotNull InfoForAppAccess details);

    /**
     * Goal: provide a response for a success registration of anonymous user.
     * @param details
     * @return on success 200 OK. Body: playerId + username + access jwt (previous won't be valid anymore).
     */
    ResponseEntity<?> forRegisterAnonymous(@NotNull InfoForAppAccess details);

    /**
     * Goal: provide a response for a success update of username and/or password
     * @param details
     * @return on success 200 OK. Body: playerId + optional if username and/or access jwt are not
     * null in accessDetails: username + access jwt.
     */
    ResponseEntity<?> forUpdateRegistered(@NotNull InfoForAppAccess details);

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
     * @param details
     * @return on success 200 OK. Body: playerId + jwts for authentication.
     */
    ResponseEntity<?> forLogin(@NotNull InfoForAppAccess details);

    /**
     * Goal: provide a resonse on success when requesting reset jwts.
     * @param details
     * @return on success 200 OK. Body: playerId + new jwts for authentication.
     */
    ResponseEntity<?> forReset(@NotNull InfoForAppAccess details);

    /**
     * Goal: provide a response on success when requesting a new access jwt.
     * @param details
     * @return on success 200 OK. Body: playerId + access jwt for authentication.
     */
    ResponseEntity<?> forAccessJwt(@NotNull InfoForAppAccess details);


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

    //GAME

    /**
     * Goal: provide a response when requesting the average win rate.
     * @param avg
     * @return on success 200 OK. Body: The average win rate.
     */
    ResponseEntity<?> forAverageWinRate(@Max(1) @Min(0) float avg);

    /**
     * Goal: provide a response when requesting the win rate
     * of an specific player
     * @param status
     * @return on success 200 OK. Body: id + username + win rate + num of rolls done
     */
    ResponseEntity<?> forWinRate(@NotNull RankedDetails status);

    /**
     * Goal: provide a response when requesting for all players.
     * @param ranking
     * @return on success 200 OK. Body: the list of players sorted
     * (id + username (can be the default) + average + number of rolls).
     */
    ResponseEntity<?> forPlayersRanked(@NotNull Collection<RankedDetails> ranking);

    /**
     * Goal: provide a response when posting a new roll.
     * Also inform if it's won or not.
     * @param roll
     * @return on success 201 CREATED. Body:
     * the values of the dices + if it's a winner roll or not + when the rolle has been done.
     */
    ResponseEntity<?> forNewRoll(@NotNull RollDetails roll);

    /**
     * Goal: provide a response when requesting for all rolss of
     * one player
     * @param rolls, not null, but can be empty
     * @returnon success 200 OK. Body: all the rolls (with the result and instant) done by the user
     */
    ResponseEntity<?> forListRolls(@NotNull Collection<RollDetails> rolls);

    /**
     * Goal: provide a response when requesting for delete
     * one player's roll. on success 204 NO CONTENT.
     * @return
     */
    ResponseEntity<?> forDeleteRolls();
}
