package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.CustomRollQueries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RollDocRepository extends MongoRepository<RollDoc, UUID>, CustomRollQueries {
}
