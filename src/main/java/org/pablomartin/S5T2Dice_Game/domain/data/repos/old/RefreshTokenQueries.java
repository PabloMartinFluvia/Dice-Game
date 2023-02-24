package org.pablomartin.S5T2Dice_Game.domain.data.repos.old;

public interface RefreshTokenQueries<T> {

    void deleteByOwner_PlayerId(T playerId);

    long countByOwner_PlayerId(T playerId);
}
