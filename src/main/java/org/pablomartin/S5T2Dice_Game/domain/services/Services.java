package org.pablomartin.S5T2Dice_Game.domain.services;

import org.pablomartin.S5T2Dice_Game.domain.models.DetailsJwt;

import java.util.UUID;

public interface Services {

    /**
     * Goal: generate new valid access and refresh jwts for this user.
     * (jwts only has authorities claims if it's interesting the user knows these
     * and can be public).
     * Note: refresh token must be created, to know the refresh token id for the refresh token claim.
     * @param ownerCredentials is a model witch contains the data required for owner's claims.
     * @return an array with the jwts (first the access, after the refresh).
     */
    String[] createJWTS(DetailsJwt ownerCredentials);

    /**
     * Goal: generate new valid access jwt for this user.
     * (jwts only has authorities claims if it's interesting the user knows these
     *  and can be public).
     * @param ownerCredentials is a model witch contains the data required for owner's claims.
     * @return the access jwt
     */
    String createAccessJWT(DetailsJwt ownerCredentials);

    /**
     * Goal: disable the posibility to be authenticated with a concrete refresh token.
     * @param refreshTokenId the specific identifier of the target refresh token.
     */
    void invalidateRefreshToken(UUID refreshTokenId);

    /**
     * Goal: disable the posibility to be authenticated with any refresh token of this user.
     * @param ownerId the specific identifier of the owner.
     */
    void invalidateAllRefreshTokensFromOwner(UUID ownerId);

    /**
     * invalidate all the refresh JWT associated to the specific owner
     * + provide new access and refresh jwt.
     * -> .invalidateAllRefreshTokensFromOwner + .createJWTS
     * @param ownerCredentials is a model witch contains the data required for owner's claims.
     * @return an array with the jwts (first the access, after the refresh).
     */
    String[] resetTokensFromOwner(DetailsJwt ownerCredentials);
}
