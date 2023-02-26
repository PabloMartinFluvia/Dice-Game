package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.RollWithoutPlayerProjection;

import java.util.UUID;
import java.util.stream.Stream;

public interface CustomRollQueries {

    Stream<RollWithoutPlayerProjection> findByPlayer_PlayerId(UUID playerId);

    void deleteByPlayer_PlayerId(UUID playerId);
}
