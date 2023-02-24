package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.PrincipalProjectionFromRefreshToken;

import java.util.Optional;
import java.util.UUID;

public interface CustomRefreshTokenQueries <ID>{

    Optional<PrincipalProjectionFromRefreshToken> findPrincipalProjectionByRefreshTokenId(ID refreshTokenId);

    void deleteByPlayer_PlayerId(UUID playerId);
}
