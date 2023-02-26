package org.pablomartin.S5T2Dice_Game.domain.data.start;

import org.pablomartin.S5T2Dice_Game.domain.data.AccessPersistenceAdapter;

public interface AdminPersistenceAdapter extends AccessPersistenceAdapter {
    boolean existsAdmin(String adminName);

    void cleanDB();
}
