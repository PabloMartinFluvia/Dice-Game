package org.pablomartin.S5T2Dice_Game.domain.data;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PersistenceAdapter {

    boolean existsPlayer(UUID playerId);

    boolean isUsernameRegistered(String username);

    boolean existsRefreshToken(UUID refreshTokenId);

    Player saveOrUpdate(Player player);

    Token saveOrUpdate(Token refreshToken);

    Optional<Player> findPlayerById(UUID playerId);

    Optional<Player> findPlayerByUsername(String username);

    Optional<Player> findOwnerByRefreshToken(UUID tokenId);

    Collection<Player> findAdmins();

    void deleteRefreshTokenById(UUID refreshTokenId);

    void deleteAllRefreshTokenFromPlayer(UUID ownerId);

    void cleanDB();

}
