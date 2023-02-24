package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.CustomPlayerQueries;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.old.PlayerQueriesOld;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface PlayerEntityRepository extends JpaRepository<PlayerEntity, UUID>, CustomPlayerQueries<UUID> {
}
