package org.pablomartin.S5T2Dice_Game.domain.data;

import org.junit.platform.suite.api.SelectClasses;
import org.junit.platform.suite.api.Suite;

@Suite
@SelectClasses({
        AccessAdapterMockTest.class,
        AdminAdapterMockTest.class,
        SecurityAdapterMockTest.class,
        AccessAdapterIT.class,
        AdminAdapterIT.class,
        GameAdapterIT.class,
        SecurityAdapterIT.class
})
public class AllAdaptersTest {
}
