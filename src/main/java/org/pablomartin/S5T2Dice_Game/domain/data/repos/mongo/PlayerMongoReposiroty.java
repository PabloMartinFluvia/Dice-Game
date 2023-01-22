package org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo;

import org.pablomartin.S5T2Dice_Game.domain.data.CommonQueries;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public interface PlayerMongoReposiroty extends MongoRepository<PlayerDoc, UUID>, CommonQueries<PlayerDoc> {



}
