package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import org.pablomartin.S5T2Dice_Game.domain.data.AccessPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.data.GamePersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AuthenticationCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.ProvidedCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.game.PlayerDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.game.RollDetails;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PlayerCredentials;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class DefaultPersistenceAdapter implements AccessPersistenceAdapter, GamePersistenceAdapter, SecurityPersistenceAdapter {

    //CREATE

    /**
     * Goal:
     * Persist new entity with the data provided + instant registry.
     * And associate it with a new valid refresh token id.
     *
     * @param providedCredentials : only username, password(encoded), role
     * @return playerId + username + role + refresh token id
     */
    @Override //DONE
    public AuthenticationCredentials newPlayerWithRefreshToken(ProvidedCredentials providedCredentials) {
        return null;
    }

    /**
     * Goal:
     * Allow a new refresh token linked to te user specified in credentials
     * and returns the credentials.
     *
     * @param credentials
     * @return the param, updated with the new refresh token id.
     * @throws PlayerNotFoundException
     */
    @Override //DONE
    public AuthenticationCredentials generateRefreshToken(AuthenticationCredentials credentials)
            throws PlayerNotFoundException {
        return null;
    }

    /**
     * Goal:
     * Save the roll (linked to the target player).
     * Note: if BD schema accepts it, also increments +1 the number of
     * rolls of the target player.
     *
     * @param playerId
     * @param roll
     * @return RollDetails with de dices + instant of roll
     * Note: doesn't contain info if it's a winner or not
     */
    @Override //DONE
    public RollDetails saveRoll(UUID playerId, RollDetails roll) {
        return null;
    }



    //READ

    /**
     * Checks if there's any player stored
     * with the provided ID.
     *
     * @param playerId
     * @return
     */
    @Override //DONE
    public boolean existsPlayer(UUID playerId) {
        return false;
    }

    @Override //DONE
    public boolean isUsernameAvailable(String username) {
        return false;
    }

    @Override //DONE
    public boolean existsRefreshToken(UUID tokenId) {
        return false;
    }

    /**
     * Goal:
     * Populates the fields that can be read directly (username, id, rolls collection).
     *
     * @param playerId
     * @return Optional empty if player not found
     */
    @Override //DONE
    public Optional<PlayerDetails> findPlayer(UUID playerId) {
        return Optional.empty();
    }

    /**
     * Goal:
     * Provide all players, only with fields that can be read directly (username, id, rolls collection).
     *
     * @return
     */
    @Override //DONE
    public List<PlayerDetails> findAllPlayers() {
        return null;
    }

    /**
     * Goal:
     * Find all rolls (linked to the target player).
     *
     * @param playerId
     * @return collection of RollDetails (can be empty).
     * Each element contain dices + instant of roll. But
     * doesn't contain info if it's a winner or not
     */
    @Override //DONE
    public List<RollDetails> findAllRolls(UUID playerId) {
        return null;
    }


    @Override //DONE
    public Optional<PlayerCredentials> loadCredentialsByUserId(UUID ownerId) {
        return Optional.empty();
    }


    /**
     * Goal: provide all the possible credentials for authentications
     *
     * @param username
     * @return if present: id + username + password + collection of granted authorities
     * Note: at least one simple granted authority "ROLE_XXX"
     */
    @Override //DONE
    public Optional<PlayerCredentials> loadCredentialsByUsername(String username) {
        return Optional.empty();
    }



    @Override //DONE
    public Optional<PlayerCredentials> loadCredentialsByRefreshTokenId(UUID tokenId) {
        return Optional.empty();
    }

    /**
     * Goal: load the role of this user.
     *
     * @param userId
     * @return Empty optional if not found.
     */
    @Override //DONE
    public Optional<Role> findUserRole(UUID userId) {
        return Optional.empty();
    }

    //UPDATE

    /**
     * Goal:
     * Update the username and password (if not null values) by the player id.
     * Also, update the role to registered if the player was anonymous.
     *
     * @param providedCredentials
     * @return
     */
    @Override //DOCE
    public AuthenticationCredentials updateCredentials(ProvidedCredentials providedCredentials) {

        return null;
    }

    //DELETE

    /**
     * Goal: remove all info (details + linked) related to the specified user.
     *
     * @param userId
     */
    @Override //DONE
    public void deleteUser(UUID userId) {

    }

    /**
     * Goal:
     * Remove all rolls linked to the target playerid.
     * Note: also remove/reset all related data IF STORED,(in mongo?)
     * like winrate, num of rolls...
     *
     * @param playerId
     */
    @Override //DONE
    public void deleteAllRolls(UUID playerId) {

    }


    /**
     * Goal:
     * Invalidate the refresh token that matches the provided id.
     *
     * @param refreshTokenId
     */
    @Override //DONE
    public void removeRefreshToken(UUID refreshTokenId) {

    }


    /**
     * Goal:
     * Invalidate all the refresh tokens linked to the specified user.
     *
     * @param ownerId
     */
    @Override //DONE
    public void deleteAllRefreshTokensByUser(UUID ownerId) {

    }


}
