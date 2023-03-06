package org.pablomartin.S5T2Dice_Game.rest.providers;

import org.pablomartin.S5T2Dice_Game.domain.models.AccessInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AverageWinRateDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.GameDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;


import java.util.List;

public interface DtoProvider {

    CredentialsDto ofFullCredentials(AccessInfo details);

    CredentialsDto ofCredentialsWithoutRefresh(AccessInfo details);

    CredentialsDto ofCredentialsWithoutUsername(AccessInfo details);

    CredentialsDto ofCredentialsOnlyAccess(AccessInfo details);

    AverageWinRateDto ofAverage(float avg);

    GameDto ofGame(RankedDetails player);

    List<GameDto> ofRanking(List<? extends RankedDetails> playersRanked);

    RollDto ofRoll(RollDetails roll);

    List<RollDto> ofRolls(List<RollDetails> rollsSorted);
}
