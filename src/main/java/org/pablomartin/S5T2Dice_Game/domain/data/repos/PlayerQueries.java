package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.Collection;
import java.util.Optional;

public interface PlayerQueries<S> {

    boolean existsByUsername(String username);

    <T> Collection<T> findByRoleIn(Collection<Role> roles,Class<T> type);

    Optional<S> findByUsername(String username);

}
