package org.pablomartin.S5T2Dice_Game.domain.data.repos.projections;

import lombok.Value;
import org.pablomartin.S5T2Dice_Game.domain.models.Game;
import org.pablomartin.S5T2Dice_Game.domain.models.GameDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;

import java.util.List;
import java.util.UUID;

@Value
public class UsernameAndId {

    UUID playerId;
    String username;

    public GameDetails toGameDetails(List<RollDetails> rolls){
        return Player.builder()
                .playerId(playerId)
                .username(username)
                .game(new Game(rolls))
                .build();
    }
}
