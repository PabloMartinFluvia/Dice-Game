package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.PlayerQueries;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;


public interface PlayerMySqlRepository extends JpaRepository<PlayerEntity, UUID> , PlayerQueries<PlayerEntity> {



}
