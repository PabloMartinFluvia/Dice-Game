package org.pablomartin.S5T2Dice_Game.domain.data;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface PersistenceAdapter {

    boolean isUsernameRegistered(String username);

    boolean existsRefreshTokenById(UUID refreshTokenId);

    Player saveNewPlayer(Player player);

    Token saveNewRefreshToken(Token refreshToken);

    Optional<Player> findPlayerById(UUID playerId);

    Optional<Player> findPlayerByUsername(String username);

    Collection<Player> findAdmins();


    void deleteAllRefreshTokens();

    void deleteAllPlayers();

    void deleteRefreshTokenById(UUID refreshTokenId);

    void deleteAllRefreshTokenFromPlayer(UUID playerId);


}
