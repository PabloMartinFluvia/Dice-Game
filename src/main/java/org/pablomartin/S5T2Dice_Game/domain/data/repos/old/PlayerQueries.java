package org.pablomartin.S5T2Dice_Game.domain.data.repos.old;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;

import java.util.Collection;
import java.util.Optional;

public interface PlayerQueries<S> {

    boolean existsByUsername(String username);

    Collection<S> findByRoleIn(Collection<Role> roles);

    Optional<S> findByUsername(String username);

}
