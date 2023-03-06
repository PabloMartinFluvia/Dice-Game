package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.AccessInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;

import java.util.UUID;

public interface AccessService {

    /**
     * Goal: creates a new access jwt.
     * @param claims is a model witch contains the data required for owner's claims.
     * @return  an AccessDetails (containing the JwtOwnerDetails) +  the access jwt
     */
    AccessInfo createAccessJWT(@NotNull SecurityClaims claims);

    //SETTING CONTROLLER

    /**
     * Goal:
     * saves a new player (based by info provided by basicCredentials)
     * + saves a new refresh Token for the created user
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * @param details: or default name and null password or username provided and password encoded.
     * @return an AccessDetails full populated.
     */
    AccessInfo performSingUp(@NotNull NewPlayerInfo details);

    /**
     * Goal: update username and/or password (only not null values) on the specified player.
     * Also, update the role to registered if the player was anonymous.
     * + if username or role changes:
     *          -> the access token won't be valid anymore ->
     *          + create a new access jwt
     *  Note: only registered or anonymous can update credentials (ADMIN NOT)
     * @param details username and/or password to update on target player (contains the id)
     * @return an AccessDetails (containing the JwtOwnerDetails) + (if created) the access jwt
     */
    AccessInfo updateCredentials(@NotNull NewPlayerInfo details);


    //AUTHENTICATION CONTROLLER

    /**
     * Goal:
     * saves a new refresh Token for that jwt owner
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * @param claims is a model witch contains the data required for owner's claims.
     * @return an AccessDetails (containing the JwtOwnerDetails) +  the access and refresh jwt created
     */
    AccessInfo createJWTS(@NotNull SecurityClaims claims);

    /**
     * Goal: invalidate all the refresh JWT associated to the specific owner
     * + saves a new refresh Token for that jwt owner
     * + creates the according refresh jwt
     * + creates a new access jwt.
     * -> .invalidateAllRefreshTokensFromOwner + .createJWTS
     * @param claims is a model witch contains the data required for owner's claims.
     * @return an AccessDetails (containing the JwtOwnerDetails) +  the access and refresh jwt created
     */
    AccessInfo resetTokensFromOwner(@NotNull SecurityClaims claims);

    /**
     * Goal: disable the possibility to be authenticated with any refresh token of this user.
     * @param playerId the specific identifier of the owner.
     */
    void invalidateAllRefreshTokensFromOwner(@NotNull UUID playerId);

    /**
     * Goal: remove all info (details + linked) related to the specified user.
     * NOTE: only if target player HAS NO ROLE ADMIN.
     * @param notAdminId id
     */
    void deleteUser(@NotNull UUID notAdminId);

    /**
     * Goal: disable the possibility to be authenticated with a concrete refresh token.
     * @param refreshTokenId the specific identifier of the target refresh token.
     */
    void invalidateRefreshToken(@NotNull UUID refreshTokenId);




}
