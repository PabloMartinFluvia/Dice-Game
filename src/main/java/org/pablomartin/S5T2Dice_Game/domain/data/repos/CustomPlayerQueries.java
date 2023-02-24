package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.OnlyRole;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.PrincipalProjection;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.UsernameAndId;

import java.util.List;
import java.util.Optional;

public interface CustomPlayerQueries<ID> {

    boolean existsByUsername(String username);

    Optional<PrincipalProjection> findPrincipalProjectionByPlayerId(ID playerId);

    Optional<PrincipalProjection> findPrincipalProjectionByUsername(String username);

    Optional<OnlyRole> findRoleProjectionByPlayerId(ID playerId);

    Optional<UsernameAndId> findUsernameByPlayerId(ID playerId);

    List<UsernameAndId> findUsernameBy(); // load all
}
