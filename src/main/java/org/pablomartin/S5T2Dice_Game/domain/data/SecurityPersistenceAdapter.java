package org.pablomartin.S5T2Dice_Game.domain.data;

import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;

import java.util.Optional;
import java.util.UUID;

public interface SecurityPersistenceAdapter {

    /**
     * Goal: provide all the possible credentials for authentications
     * @param username username
     * @return if present: id + username + password + collection of granted authorities
     * Note: at least one simple granted authority "ROLE_XXX"
     */
    Optional<PrincipalProvider> loadCredentialsByUsername(String username); //basif auth

    //idem
    Optional<PrincipalProvider> loadCredentialsByUserId(UUID userId); //access jwt

    //idem
    Optional<PrincipalProvider> loadCredentialsByRefreshTokenId(UUID tokenId); //refresh jwt

    boolean existsRefreshToken(UUID tokenId);

    void removeRefreshToken(UUID tokenId);
}
