package org.pablomartin.S5T2Dice_Game.domain.data.start;

import org.pablomartin.S5T2Dice_Game.domain.data.SettingsPersistenceAdapter;

public interface AdminPersistenceAdapter extends SettingsPersistenceAdapter {
    boolean existsAdmin(String adminName);

    void cleanDB();
}
