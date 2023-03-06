package org.pablomartin.S5T2Dice_Game.domain.data;

import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;

import java.util.Optional;
import java.util.UUID;

public interface SettingsPersistenceAdapter {

    /**
     * Checks if there's any player stored
     * with the provided ID.
     * @param playerId id
     * @return if exists
     */
    boolean existsPlayer(@NotNull UUID playerId);

    boolean isUsernameAvailable(@NotNull String username);

    /**
     * Goal:
     * Persist new entity with the data provided + instant registry.
     * And associate it with a new valid refresh token id.
     * @param details: provides username (maybe default),
     *      *                            password (null or encoded),
     *      *                            role already set
     * @return playerId + username + role + refresh token id
     */
    SecurityClaims newPlayerWithRefreshToken(@NotNull NewPlayerInfo details);

    /**
     * Goal:
     * Update the username and password (if not null values) by the player id.
     * Also, update the role to registered if the player was anonymous.
     * @param details model with the new data
     * @return model with persisted updated data
     */
    SecurityClaims updateCredentials(@NotNull NewPlayerInfo details);

    /**
     * Goal: load the role of this user.
     * @param playerId id
     * @return Empty optional if not found.
     */
    Optional<Role> findUserRole(@NotNull UUID playerId);

    /**
     * Goal: remove all info (details + linked) related to the specified user.
     * @param playerId id
     */
    void deleteUser(@NotNull UUID playerId);


    /**
     * Goal:
     * Allow a new refresh token linked to te user specified in credentials
     * and returns the credentials.
     * @param claims model with data
     * @return the param, updated with the new refresh token id.
     * @throws PlayerNotFoundException if player to link the new token not found
     */
    SecurityClaims allowNewRefreshToken(@NotNull SecurityClaims claims) throws PlayerNotFoundException;

    /**
     * Goal:
     * Invalidate the refresh token that matches the provided id.
     * @param refreshTokenId id
     */
    void removeRefreshToken(@NotNull UUID refreshTokenId);

    /**
     * Goal:
     * Invalidate all the refresh tokens linked to the specified user.
     * @param playerId id
     */
    void deleteAllRefreshTokensByUser(UUID playerId);
}
