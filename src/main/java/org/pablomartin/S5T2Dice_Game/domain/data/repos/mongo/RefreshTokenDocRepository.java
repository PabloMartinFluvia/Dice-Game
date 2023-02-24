package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.CustomRefreshTokenQueries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface RefreshTokenDocRepository extends MongoRepository<RefreshTokenDoc, UUID>, CustomRefreshTokenQueries<UUID> {
}
