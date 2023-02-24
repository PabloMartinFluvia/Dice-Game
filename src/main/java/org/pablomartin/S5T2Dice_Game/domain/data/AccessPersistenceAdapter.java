package org.pablomartin.S5T2Dice_Game.domain.data;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface AccessPersistenceAdapter {

    /**
     * Checks if there's any player stored
     * with the provided ID.
     * @param playerId
     * @return
     */
    boolean existsPlayer(@NotNull UUID playerId);

    boolean isUsernameAvailable(@NotNull String username);

    /**
     * Goal:
     * Persist new entity with the data provided + instant registry.
     * And associate it with a new valid refresh token id.
     * @param newPlayerInfo: only username, password(encoded), role
     * @return playerId + username + role + refresh token id
     */
    SecurityClaims newPlayerWithRefreshToken(@NotNull NewPlayerInfo newPlayerInfo);

    /**
     * Goal:
     * Update the username and password (if not null values) by the player id.
     * Also, update the role to registered if the player was anonymous.
     * @param newPlayerInfo
     * @return
     */
    SecurityClaims updateCredentials(@NotNull NewPlayerInfo newPlayerInfo);

    /**
     * Goal: load the role of this user.
     * @param userId
     * @return Empty optional if not found.
     */
    Optional<Role> findUserRole(@NotNull UUID userId);

    /**
     * Goal: remove all info (details + linked) related to the specified user.
     * @param userId
     */
    void deleteUser(@NotNull UUID userId);


    /**
     * Goal:
     * Allow a new refresh token linked to te user specified in credentials
     * and returns the credentials.
     * @param credentials
     * @return the param, updated with the new refresh token id.
     * @throws PlayerNotFoundException
     */
    SecurityClaims allowNewRefreshToken(@NotNull SecurityClaims credentials) throws PlayerNotFoundException;

    /**
     * Goal:
     * Invalidate the refresh token that matches the provided id.
     * @param refreshTokenId
     */
    void removeRefreshToken(@NotNull UUID refreshTokenId);

    /**
     * Goal:
     * Invalidate all the refresh tokens linked to the specified user.
     * @param ownerId
     */
    void deleteAllRefreshTokensByUser(UUID ownerId);
}
