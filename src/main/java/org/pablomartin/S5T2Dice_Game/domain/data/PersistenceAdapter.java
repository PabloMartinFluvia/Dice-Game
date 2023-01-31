package org.pablomartin.S5T2Dice_Game.domain.data;

import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PersistenceAdapter {

    boolean existsPlayer(UUID playerId);

    boolean isUsernameRegistered(String username);

    boolean existsRefreshToken(UUID refreshTokenId);

    PlayerOld saveOrUpdate(PlayerOld playerOld);

    Token saveOrUpdate(Token refreshToken);

    Optional<PlayerOld> findPlayerById(UUID playerId);

    Optional<PlayerOld> findPlayerByUsername(String username);

    Optional<PlayerOld> findOwnerByRefreshToken(UUID tokenId);

    Collection<PlayerOld> findAdmins();

    void deleteRefreshTokenById(UUID refreshTokenId);

    void deleteAllRefreshTokenFromPlayer(UUID ownerId);

    void cleanDB();

}
