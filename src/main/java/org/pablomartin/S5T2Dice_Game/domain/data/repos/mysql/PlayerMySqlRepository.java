package org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql;

import org.pablomartin.S5T2Dice_Game.domain.data.CommonQueries;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;


public interface PlayerMySqlRepository extends JpaRepository<PlayerEntity, UUID> , CommonQueries<PlayerEntity> {



}
