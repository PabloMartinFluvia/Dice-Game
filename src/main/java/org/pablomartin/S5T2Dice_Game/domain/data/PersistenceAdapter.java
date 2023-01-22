package org.pablomartin.S5T2Dice_Game.domain.data;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.RefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface PersistenceAdapter {

    public boolean isUsernameRegistered(String username);

    Player saveNewPlayer(Player player);

    RefreshToken saveNewRefreshToken(RefreshToken refreshToken);

    Optional<Player> findPlayerByUsername(String username);

    Optional<Player> findPlayerById(UUID playerId);
}
