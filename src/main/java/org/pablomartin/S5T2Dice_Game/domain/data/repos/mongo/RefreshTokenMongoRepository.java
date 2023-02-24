package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.old.RefreshTokenQueries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RefreshTokenMongoRepository extends MongoRepository<RefreshTokenDoc, UUID>, RefreshTokenQueries<UUID> {
}
