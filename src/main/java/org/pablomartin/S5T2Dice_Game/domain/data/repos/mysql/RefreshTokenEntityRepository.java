package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.CustomRefreshTokenQueries;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.old.RefreshTokenQueriesOld;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenEntityRepository extends JpaRepository<RefreshTokenEntity, UUID>, CustomRefreshTokenQueries<UUID> {
}
