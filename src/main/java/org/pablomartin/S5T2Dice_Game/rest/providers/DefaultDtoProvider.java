package org.pablomartin.S5T2Dice_Game.rest.providers;

import org.pablomartin.S5T2Dice_Game.domain.models.InfoForAppAccess;
import org.pablomartin.S5T2Dice_Game.domain.models.RankedDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AverageWinRateDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.GameDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class DefaultDtoProvider implements DtoProvider{

    public CredentialsDto ofFullCredentials(InfoForAppAccess details){
        return CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .username(details.getUsername()) // may be null when sing up as anonymous
                .accessJwt(details.getAccessJwt())
                .refreshJwt(details.getRefreshJwt())
                .build();
    }

    public CredentialsDto ofCredentialsWithoutRefresh(InfoForAppAccess details){
        return  CredentialsDto.builder()
                .playerId(details.getPlayerId())
                // may be null if registered wants to update only password
                .username(details.getUsername())
                // may be null if registered wants to update only password (old access jwt still valid)
                .accessJwt(details.getAccessJwt())
                .build();
    }

    public CredentialsDto ofCredentialsWithoutUsername(InfoForAppAccess details){
        return CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .accessJwt(details.getAccessJwt())
                .refreshJwt(details.getRefreshJwt())
                .build();
    }

    public CredentialsDto ofCredentialsOnlyAccess(InfoForAppAccess details){
        return CredentialsDto.builder()
                .playerId(details.getPlayerId())
                .accessJwt(details.getAccessJwt())
                .build();
    }

    public AverageWinRateDto ofAverage(float avg){
        return new AverageWinRateDto(avg);
    }

    public GameDto ofGame(RankedDetails player){
        return GameDto.builder()
                .playerId(player.getPlayerId())
                .username(player.getUsername())
                .numRolls(player.getNumRolls())
                .winRate(player.getWinRate())
                .build();
    }

    public List<GameDto> ofRanking(List<? extends RankedDetails> playersRanked){
        List<GameDto> dtos = new LinkedList<>();
        for(RankedDetails player: playersRanked){
            dtos.add(ofGame(player));
        }
        return dtos;
    }

    public RollDto ofRoll(RollDetails roll){
        return RollDto.builder()
                .dicesValues(roll.getDicesValues())
                .result(roll.isWon()?"WIN":"LOOSE")
                .instant(roll.getInstantRoll())
                .build();
    }

    public List<RollDto> ofRolls(List<RollDetails> rollsSorted){
        List<RollDto> dtos = new LinkedList<>();
        for(RollDetails roll: rollsSorted){
            dtos.add(ofRoll(roll));
        }
        return dtos;
    }
}
