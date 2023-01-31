package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AccessDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.BasicCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.JwtCredentialsProvider;
import org.pablomartin.S5T2Dice_Game.domain.models.game.RollDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.StatusDetails;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.UUID;

public interface Services {

    //SETTING CONTROLLER

    /**
     * Goal:
     * saves a new player (based by info provided by basicCredentials)
     * + saves a new refresh Token for the created user
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * @param basicCredentials: or default name and null password or username provided and password encoded.
     * @return an AccessDetails full populated.
     */
    AccessDetails performSingUp(@NotNull BasicCredentials basicCredentials);

    /**
     * Goal: update username and/or password (only not null values) on the specified player
     * + also assert persisted role is registered if username and password are provided
     * + if username or role changes:
     *          -> the access token won't be valid anymore ->
     *          + create a new access jwt
     * @param playerId target player to update
     * @param basicCredentials username and/or password to update on target player
     * @return an AccessDetails (containing the JwtOwnerDetails) + (if created) the access jwt
     */
    AccessDetails updateCredentials(@NotNull UUID playerId, @NotNull BasicCredentials basicCredentials);

    /**
     * Goal: remove all info (details + linked) related to the specified user.
     * NOTE: only if target player HAS NOT ROLE ADMIN.
     * @param targetNotAdminUserId
     */
    void deleteUser(@NotNull UUID targetNotAdminUserId);

    /**
     * Goal: change the role of the specified user to ADMIN.
     * NOTE: only if target user HAS ROLE REGISTERED.
     * @param targetRegisteredUserId
     */
    void promoteUser(@NotNull UUID targetRegisteredUserId);

    //AUTHENTICATION CONTROLLER

    /**
     * Goal:
     * saves a new refresh Token for that jwt owner
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * @param ownerDetails is a model witch contains the data required for owner's claims.
     * @return an AccessDetails (containing the JwtOwnerDetails) +  the access and refresh jwt created
     */
    AccessDetails createJWTS(@NotNull JwtCredentialsProvider ownerDetails);

    /**
     * Goal: creates a new access jwt.
     * @param ownerDetails is a model witch contains the data required for owner's claims.
     * @return  an AccessDetails (containing the JwtOwnerDetails) +  the access jwt
     */
    AccessDetails createAccessJWT(@NotNull JwtCredentialsProvider ownerDetails);

    /**
     * Goal: disable the posibility to be authenticated with a concrete refresh token.
     * @param refreshTokenId the specific identifier of the target refresh token.
     */
    void invalidateRefreshToken(@NotNull UUID refreshTokenId);

    /**
     * Goal: disable the posibility to be authenticated with any refresh token of this user.
     * @param ownerId the specific identifier of the owner.
     */
    void invalidateAllRefreshTokensFromOwner(@NotNull UUID ownerId);

    /**
     * Goal: invalidate all the refresh JWT associated to the specific owner
     * + saves a new refresh Token for that jwt owner
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * -> .invalidateAllRefreshTokensFromOwner + .createJWTS
     * @param ownerDetails is a model witch contains the data required for owner's claims.
     * @return an AccessDetails (containing the JwtOwnerDetails) +  the access and refresh jwt created
     */
    AccessDetails resetTokensFromOwner(@NotNull JwtCredentialsProvider ownerDetails);

    //GAME CONTROLLER

    /**
     * Goal: calculate the average win rate of the players.
     * Note: players without any roll done are ignored.
     * @return the average win rate
     */
    float loadAverageWinRate();

    /**
     * Goal: provide a linked set of players sorted by their
     *  average win rate (and number of rolls done).
     *  Note: includes ALL players (even those with no rolls done).
     * @return the linked set.
     */
    LinkedHashSet<StatusDetails> loadPlayersRanked();

    /**
     * Goal: save the roll (linked to the target player).
     * Note: if BD schema accepts it, also increments +1 the number of
     * rolls of the target player.
     * @param targetPlayerId
     * @param roll
     * @return the rolls details, include if the roll is a winner one or not.
     */
    RollDetails saveNewRoll(@NotNull UUID targetPlayerId, @NotNull RollDetails roll);

    /**
     * Goal: load the player's roll (Collection of player's rolls)
     * + inform, for each one, if it's a winner one
     * @param playerId
     * @return all rolls details, include if the roll is a winner one or not.
     */
    Collection<RollDetails> loadRolls(@NotNull UUID playerId);

    /**
     * Goal: provide a full populated StatusDetails for the specific player.
     * @param playerId
     * @return id + username + win rate + num of rolls done. If the player has no rolls done
     * the win rate is 0%.
     */
    StatusDetails loadWinRate(@NotNull UUID playerId);

    /**
     * Goal: remove all rolls linked to the target player.
     * Note: also remove/reset all related data IF STORED,
     * like winrate, num of rolls...
     * @param playerId
     */
    void deleteRolls(@NotNull UUID playerId);
}
