package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AccessDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.ProvidedCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AuthenticationCredentials;

import java.util.UUID;

public interface AccessService {

    /**
     * Goal: creates a new access jwt.
     * @param ownerDetails is a model witch contains the data required for owner's claims.
     * @return  an AccessDetails (containing the JwtOwnerDetails) +  the access jwt
     */
    AccessDetails createAccessJWT(@NotNull AuthenticationCredentials ownerDetails);

    //SETTING CONTROLLER

    /**
     * Goal:
     * saves a new player (based by info provided by basicCredentials)
     * + saves a new refresh Token for the created user
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * @param credentials: or default name and null password or username provided and password encoded.
     * @return an AccessDetails full populated.
     */
    AccessDetails performSingUp(@NotNull ProvidedCredentials credentials);

    /**
     * Goal: update username and/or password (only not null values) on the specified player.
     * Also, update the role to registered if the player was anonymous.
     * + if username or role changes:
     *          -> the access token won't be valid anymore ->
     *          + create a new access jwt
     *  Note: only registered or anonymous can update credentials (ADMIN NOT)
     * @param credentials username and/or password to update on target player (contains the id)
     * @return an AccessDetails (containing the JwtOwnerDetails) + (if created) the access jwt
     */
    AccessDetails updateCredentials(@NotNull ProvidedCredentials credentials);


    //AUTHENTICATION CONTROLLER

    /**
     * Goal:
     * saves a new refresh Token for that jwt owner
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * @param ownerDetails is a model witch contains the data required for owner's claims.
     * @return an AccessDetails (containing the JwtOwnerDetails) +  the access and refresh jwt created
     */
    AccessDetails createJWTS(@NotNull AuthenticationCredentials ownerDetails);

    /**
     * Goal: invalidate all the refresh JWT associated to the specific owner
     * + saves a new refresh Token for that jwt owner
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * -> .invalidateAllRefreshTokensFromOwner + .createJWTS
     * @param ownerDetails is a model witch contains the data required for owner's claims.
     * @return an AccessDetails (containing the JwtOwnerDetails) +  the access and refresh jwt created
     */
    AccessDetails resetTokensFromOwner(@NotNull AuthenticationCredentials ownerDetails);

    /**
     * Goal: disable the posibility to be authenticated with any refresh token of this user.
     * @param ownerId the specific identifier of the owner.
     */
    void invalidateAllRefreshTokensFromOwner(@NotNull UUID ownerId);

    /**
     * Goal: remove all info (details + linked) related to the specified user.
     * NOTE: only if target player HAS NOT ROLE ADMIN.
     * @param targetNotAdminUserId
     */
    void deleteUser(@NotNull UUID targetNotAdminUserId);

    /**
     * Goal: disable the posibility to be authenticated with a concrete refresh token.
     * @param refreshTokenId the specific identifier of the target refresh token.
     */
    void invalidateRefreshToken(@NotNull UUID refreshTokenId);




}
