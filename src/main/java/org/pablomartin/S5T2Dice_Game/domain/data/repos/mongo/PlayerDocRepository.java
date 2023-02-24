package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.CustomPlayerQueries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PlayerDocRepository extends MongoRepository<PlayerDoc, UUID>, CustomPlayerQueries<UUID> {
}
