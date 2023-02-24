package org.pablomartin.S5T2Dice_Game.domain.data;

import org.pablomartin.S5T2Dice_Game.security.principalsModels.PlayerCredentials;

import java.util.Optional;
import java.util.UUID;

public interface SecurityPersistenceAdapter {

    /**
     * Goal: provide all the possible credentials for authentications
     * @param username
     * @return if present: id + username + password + collection of granted authorities
     * Note: at least one simple granted authority "ROLE_XXX"
     */
    Optional<PlayerCredentials> loadCredentialsByUsername(String username);

    //idem
    Optional<PlayerCredentials> loadCredentialsByUserId(UUID userId);

    Optional<PlayerCredentials> loadCredentialsByRefreshTokenId(UUID tokenId);

    boolean existsRefreshToken(UUID tokenId);

    void removeRefreshToken(UUID tokenId);
}
