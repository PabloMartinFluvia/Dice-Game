package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.PlayerQueries;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.UUID;

public interface PlayerMongoReposiroty extends MongoRepository<PlayerDoc, UUID>, PlayerQueries<PlayerDoc> {



}
