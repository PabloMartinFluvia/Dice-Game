package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pablomartin.S5T2Dice_Game.domain.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)  //magrate the test to Junit5
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
public class GameAdapterIT {

    @Autowired
    private GamePersistenceAdapter adapter;

    @Autowired
    private SettingsPersistenceAdapter auxiliarAccessAdapter;

    //@Test //test disbled. Implementation shared with AccessPersistenceAdapter. Tested in AccessAdapterIT
    public void existsPlayerTest(){
        UUID playerId = addNewPlayer();
        assertTrue(adapter.existsPlayer(playerId), "player not found");
        auxiliarAccessAdapter.deleteUser(playerId);
    }

    @Test
    public void addRollTest(){
        UUID playerId = addNewPlayer();
        int previusNum = adapter.findAllRolls(playerId).size();

        int[] dices = new int[]{3,2};
        RollDetails roll = new Roll(dices);
        RollDetails result = adapter.saveRoll(playerId,roll);
        assertEquals(previusNum+1,adapter.findAllRolls(playerId).size(), "num of rolls not +1");
        //dices array sorted for equals check
        Arrays.sort(dices);
        Arrays.sort(result.getDicesValues());
        assertEquals(dices,result.getDicesValues(),"Stored array does not contain the sames values");
        assertNotNull(result.getInstantRoll(),"instant of roll is not returned");
        assertNotNull(result.getRollId(),"rollId is not saved");
        auxiliarAccessAdapter.deleteUser(playerId); //implies remove all
    }

    @Test
    public void findAllRollsFromUserTest(){
        UUID playerId = addNewPlayer();
        assertTrue(adapter.findAllRolls(playerId).isEmpty(),"some rolls found for a player without rolls");

        List<RollDetails> saved = addSomeRollsForExistingPlayer(playerId);
        List<RollDetails> found = adapter.findAllRolls(playerId);
        assertEquals(saved.size(),found.size(), "size of found rolls does not match");
        assertTrue(found.containsAll(saved), "not all saved rolls are loaded");
        auxiliarAccessAdapter.deleteUser(playerId);

        found =adapter.findAllRolls(getInvalidPlayerId());
        assertTrue(found.isEmpty(),"some rolls loaded for an inexistent player");
    }

    @Test
    public void deleteAllRollsFromUserTest(){
        UUID playerId = addNewPlayer();
        List<RollDetails> saved = addSomeRollsForExistingPlayer(playerId);
        assertFalse(saved.isEmpty(),"player not has rolls saved");
        adapter.deleteAllRolls(playerId);
        assertTrue(adapter.findAllRolls(playerId).isEmpty(),"there are roll(s) related to the player");
        auxiliarAccessAdapter.deleteUser(playerId);
    }

    @Test
    public void findPlayerDetailsTest(){
        String username = "bla";
        UUID playerId = addNewPlayer(username);
        List<RollDetails> saved = addSomeRollsForExistingPlayer(playerId);

        Optional<GameDetails> found = adapter.findGame(playerId);
        assertTrue(found.isPresent(),"player is not found");
        GameDetails player = found.orElse(null);
        assertEquals(playerId,player.getPlayerId(),"ids not match");
        assertEquals(username,player.getUsername(),"usernames not match");
        assertTrue(player.getRolls().isPresent(),"rolls not stored");
        assertEquals(saved.size(),player.getRolls().get().size(),"nums of rolls not match");
        assertTrue(player.getRolls().get().containsAll(saved),"not all saved are loaded");
        auxiliarAccessAdapter.deleteUser(playerId);

        found =adapter.findGame(getInvalidPlayerId());
        assertTrue(found.isEmpty(),"invalid player has been found");
    }

    @Test
    public void findAllPlayersTest(){
        int previosNumPlayers = adapter.findAllGames().size();

        UUID playerIdA = addNewPlayer(); //anonymous
        String usernameR = "x";
        UUID playerIdR = addNewPlayer(usernameR); //registered
        List<RollDetails> savedA = addSomeRollsForExistingPlayer(playerIdA); //3 rolls
        List<RollDetails> savedR = List.of(adapter.saveRoll(playerIdR, new Roll(new int[]{2,2}))); //1 roll

        List<GameDetails> found =adapter.findAllGames();
        assertEquals(previosNumPlayers+2,found.size(),"not found exactly +2 players");
        GameDetails foundA = found.stream()
                .filter(details -> details.getPlayerId().equals(playerIdA))
                .findFirst().orElse(null);
        GameDetails foundR = found.stream()
                .filter(details -> details.getPlayerId().equals(playerIdR))
                .findFirst().orElse(null);

        String usernameA = DiceGamePathsContext.getDefaultUsername();
        assertEquals(usernameA,foundA.getUsername(),"username anonymous not match");
        assertEquals(usernameR,foundR.getUsername(),"username registered not match");
        assertTrue(foundA.getRolls().isPresent(),"rolls from anonymous not stored");
        assertTrue(foundR.getRolls().isPresent(),"rolls from registered not stored");

        assertEquals(savedA.size(),foundA.getRolls().get().size(),"nums of rolls A not match");
        assertEquals(savedR.size(),foundR.getRolls().get().size(),"nums of rolls R not match");
        assertTrue(foundA.getRolls().get().containsAll(savedA),"not all saved A are loaded");
        assertTrue(foundR.getRolls().get().containsAll(savedR),"not all saved R are loaded");

        auxiliarAccessAdapter.deleteUser(playerIdA);
        auxiliarAccessAdapter.deleteUser(playerIdR);
    }

    private UUID addNewPlayer(){
        return auxiliarAccessAdapter.newPlayerWithRefreshToken(Player.asVisitor())
                .getPlayerId();
    }

    private UUID addNewPlayer(String username){
        return auxiliarAccessAdapter.newPlayerWithRefreshToken(Player.asRegistered(username,null))
                .getPlayerId();
    }

    private List<RollDetails> addSomeRollsForExistingPlayer(UUID playerId){
        List<RollDetails> rollsDone = List.of(
                new Roll(new int[]{1,2}),new Roll(new int[]{3,4}),new Roll(new int[]{5,6}));
        List<RollDetails> saved = new ArrayList<>();
        for(RollDetails roll: rollsDone){
            saved.add(adapter.saveRoll(playerId,roll));
        }
        return saved;
    }
    private UUID getInvalidPlayerId(){
        UUID id;
        do {
            id = UUID.randomUUID();

        }while (adapter.existsPlayer(id));
        return id;
    }
}
