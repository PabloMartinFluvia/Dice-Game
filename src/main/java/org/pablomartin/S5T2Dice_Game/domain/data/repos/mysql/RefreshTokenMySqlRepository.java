package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.RefreshTokenQueries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface RefreshTokenMySqlRepository extends JpaRepository<RefreshTokenEntity, UUID>, RefreshTokenQueries<UUID> {
}
