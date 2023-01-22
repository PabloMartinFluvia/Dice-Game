package org.pablomartin.S5T2Dice_Game.domain.data;

import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CommonQueries<S> {

    boolean existsByUsername(String username);

    <T> Collection<T> findByRoleIn(Collection<Role> roles,Class<T> type);

    Optional<S> findByUsername(String username);
}
