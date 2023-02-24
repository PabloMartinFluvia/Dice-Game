package org.pablomartin.S5T2Dice_Game.domain.data.Test;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * MongoDB's relations:
 * DBRef and documented reference.
 * When loaded both includes the object in the annotated field.
 * DBREF stores it in dbref format
 * Documented reference sotres it in whatever format I want, usually only by id
 *
 * IMPORTANT: DBREF and documented reference does not support CASCADE operations
 */

public class PlayerTest {

    UUID playerId; // autogenerated

    PersonalInfoTest userDetails; // 1:1

    GameInfoTest gameDetails; //1:1

    LocalDateTime instant; //when sing up


}
