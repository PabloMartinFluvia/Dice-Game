package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.AccessDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.BasicCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.JwtOwnerDetails;

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
    AccessDetails createJWTS(@NotNull JwtOwnerDetails ownerDetails);

    /**
     * Goal: creates a new access jwt.
     * @param ownerDetails is a model witch contains the data required for owner's claims.
     * @return  an AccessDetails (containing the JwtOwnerDetails) +  the access jwt
     */
    AccessDetails createAccessJWT(@NotNull JwtOwnerDetails ownerDetails);

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
    AccessDetails resetTokensFromOwner(@NotNull JwtOwnerDetails ownerDetails);



}
