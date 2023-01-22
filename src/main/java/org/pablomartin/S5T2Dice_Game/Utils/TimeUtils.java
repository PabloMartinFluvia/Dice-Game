package org.pablomartin.S5T2Dice_Game.Utils;

import java.time.Clock;
import java.time.LocalDateTime;

public class TimeUtils {

    public static LocalDateTime nowSecsTruncated(){
        return LocalDateTime.now(Clock.tickSeconds(Clock.systemDefaultZone().getZone()));
    }
}
