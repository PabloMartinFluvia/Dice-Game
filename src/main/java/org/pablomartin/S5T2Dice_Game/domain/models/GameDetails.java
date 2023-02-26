package org.pablomartin.S5T2Dice_Game.domain.models;

import java.util.List;
import java.util.Optional;

public interface GameDetails extends RankedDetails {

    void calculateWinRate();

    Optional<List<RollDetails>> getRolls(); //for testing purposes

}
