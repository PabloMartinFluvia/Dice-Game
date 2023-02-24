package org.pablomartin.S5T2Dice_Game.domain.data.start;

import org.pablomartin.S5T2Dice_Game.domain.data.AccessPersistenceAdapter;

public interface AdminAdapter extends AccessPersistenceAdapter {
    boolean existsAdmin(String admin_pablo);

    void cleanDB();
}
