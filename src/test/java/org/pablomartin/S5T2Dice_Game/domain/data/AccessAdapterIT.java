package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pablomartin.S5T2Dice_Game.domain.data.AccessPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.data.GamePersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.*;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

/*
IMPORTANT: mateixes annotacions per a totes les classes de testos,
per així evitar que Spring aixequi un nou context per a cada test.
 */
//@RunWith(SpringRunner.class) // runner for JUnit4 -> creates problems with JUnit5 annotations
@ExtendWith(SpringExtension.class)  //magrate the test to Junit5
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
public class AccessAdapterIT {

    @Autowired
    private AccessPersistenceAdapter adapter;

    @Autowired
    private SecurityPersistenceAdapter auxiliarSecurityAdapter;

    @Autowired
    private GamePersistenceAdapter auxiliarGameAdapter;

    @Test
    public void existsPlayerTest(){
        UUID playerId = adapter.newPlayerWithRefreshToken(Player.asAnnonimous()).getPlayerId();
        assertTrue(adapter.existsPlayer(playerId), "player not found");
        adapter.deleteUser(playerId);
    }

    @Test
    public void isUsernameAvailableTest(){
        String username = "x"; // don't exists any player with this username, due app constraints
        assertTrue(adapter.isUsernameAvailable(username), "username not available");
        UUID playerId = adapter.newPlayerWithRefreshToken(Player.asRegistered(username,null)).getPlayerId();
        assertFalse(adapter.isUsernameAvailable(username), "username available");
        adapter.deleteUser(playerId);
    }

    @ParameterizedTest
    @MethodSource("newSingupRequests") //if name not provided -> JUnit will find a private static method with same name
    /*
    Note: method must be static -> if beans are used: injections in instance fields
    still are not done.
    https://www.arhohuttunen.com/junit-5-parameterized-tests/
    https://www.baeldung.com/parameterized-tests-junit-5
     */
    public void newPlayerWithRefreshTokenTest(NewPlayerInfo playerInfo){

        SecurityClaims persistedData = adapter.newPlayerWithRefreshToken(playerInfo);

        assertNotNull(persistedData, "returned is null");
        assertNotNull(persistedData.getPlayerId(), "playerId is null");
        assertEquals(playerInfo.getUsername(),persistedData.getUsername(), "username not matches");
        assertEquals(playerInfo.getRole(),persistedData.getRole(), "roles not matches");
        assertNotNull(persistedData.getRefreshTokenId(), "refreshTokenId is null");
        //just for check password is not stored in instance returned after persist
        assertNull(((Player)persistedData).getPasswordEncoded(), "passwor is not null");
        //log.info("--> "+persistedData);
        adapter.deleteUser(persistedData.getPlayerId());
    }

    @Test
    public void updateUserTest(){
        SecurityClaims saved = adapter.newPlayerWithRefreshToken(Player.asAnnonimous());
        UUID playerId = saved.getPlayerId();

        //anonymous wants to register
        String username = "nameV1";
        String passwordEncoded = "passwordV1";
        //note: if username and password provided not null in dto -> model instance always has role registered
        NewPlayerInfo playerInfo = Player.asRegistered(username,passwordEncoded);
        playerInfo.setPlayerId(playerId); //when updating the id is setted
        SecurityClaims result = adapter.updateCredentials(playerInfo);
        assertEquals(username,result.getUsername(), "username has not been updated");
        assertEquals(Role.REGISTERED, result.getRole(),"role has not been updated");
        //to verify password stored updated
        String passwordStored = auxiliarSecurityAdapter.loadCredentialsByUserId(playerId).orElse(null).getPassword();
        assertEquals(passwordEncoded,passwordStored, "password has not been updated");

        //registered wants to update username
        username = "nameV2";
        playerInfo = Player.asRegistered(username,null);
        playerInfo.setPlayerId(playerId);
        result = adapter.updateCredentials(playerInfo);
        assertEquals(username,result.getUsername(), "username has not been updated");
        assertEquals(Role.REGISTERED, result.getRole(),"role is no longer registered");
        //to verify password stored not updated
        passwordStored = auxiliarSecurityAdapter.loadCredentialsByUserId(playerId).orElse(null).getPassword();
        assertEquals(passwordEncoded,passwordStored, "password has been modified");

        //registered wants to upsate password
        passwordEncoded = "passwordV2";
        playerInfo = Player.asRegistered(null,passwordEncoded);
        playerInfo.setPlayerId(playerId);
        result = adapter.updateCredentials(playerInfo);
        assertEquals(username,result.getUsername(), "username has been modified");
        assertEquals(Role.REGISTERED, result.getRole(),"role is no longer registered");
        //to verify password stored updated
        passwordStored = auxiliarSecurityAdapter.loadCredentialsByUserId(playerId).orElse(null).getPassword();
        assertEquals(passwordEncoded,passwordStored, "password has not been updated");

        //registered wants to update both
        username = "nameV3";
        passwordEncoded = "passwordV3";
        playerInfo = Player.asRegistered(username,passwordEncoded);
        playerInfo.setPlayerId(playerId);
        result = adapter.updateCredentials(playerInfo);
        assertEquals(username,result.getUsername(), "username has not been updated");
        assertEquals(Role.REGISTERED, result.getRole(),"role is no longer registered");
        //to verify password stored updated
        passwordStored = auxiliarSecurityAdapter.loadCredentialsByUserId(playerId).orElse(null).getPassword();
        assertEquals(passwordEncoded,passwordStored, "password has not been updated");

        //other combos not tested, it's not allowed (tested in controllers when validating requests)
        adapter.deleteUser(playerId);
    }

    @ParameterizedTest
    @MethodSource("newSingupRequests")
    public void findRoleTest(NewPlayerInfo player){
        Role role = player.getRole();
        SecurityClaims stored = adapter.newPlayerWithRefreshToken(player);
        UUID playerId = stored.getPlayerId();

        Optional<Role> roleFound =
                adapter.findUserRole(playerId);
        assertTrue(roleFound.isPresent(),"user role not found.");
        assertEquals(role,roleFound.get(),"role not equals");
        adapter.deleteUser(playerId);
    }



    @Test
    public void deleteUserTest(){
        SecurityClaims credentials = adapter.newPlayerWithRefreshToken
                (Player.asRegistered("a","b"));
        UUID playerId = credentials.getPlayerId();
        UUID tokenId1 = credentials.getRefreshTokenId();
        UUID tokenId2 = adapter.allowNewRefreshToken(credentials).getRefreshTokenId();
        assertFalse(tokenId1.equals(tokenId2),"tokens id must be different");
        List<RollDetails> rollsDone = List.of(new Roll(new int[]{1,2}),new Roll(new int[]{3,4}));
        rollsDone.forEach(roll -> auxiliarGameAdapter.saveRoll(playerId,roll));

        adapter.deleteUser(playerId);
        assertFalse(adapter.existsPlayer(playerId),"player is not removed");
        assertFalse(auxiliarSecurityAdapter.existsRefreshToken(tokenId1),"token 1 is not removed");
        assertFalse(auxiliarSecurityAdapter.existsRefreshToken(tokenId2),"token 2 is not removed");
        assertTrue(auxiliarGameAdapter.findAllRolls(playerId).isEmpty(),"rolls not removed");
    }

    @ParameterizedTest
    @MethodSource("newSingupRequests")
    public void allowNewRefreshTokenForValidUserTest(NewPlayerInfo newPlayerInfo){
        log.info("------> "+newPlayerInfo);
        SecurityClaims existingData = adapter.newPlayerWithRefreshToken(newPlayerInfo);
        UUID playerId = existingData.getPlayerId();
        UUID old = existingData.getRefreshTokenId();
        SecurityClaims withNewToken = adapter.allowNewRefreshToken(existingData);
        UUID newToken = withNewToken.getRefreshTokenId();
        assertTrue(!newToken.equals(old), "new security claims does not store a new id for the refresh token");
        assertEquals(existingData.getUsername(),withNewToken.getUsername(), "username is different");
        assertEquals(existingData.getRole(),withNewToken.getRole(), "role is different");
        assertTrue(auxiliarSecurityAdapter.existsRefreshToken(newToken), "new token not stored");
        assertTrue(auxiliarSecurityAdapter.loadCredentialsByRefreshTokenId(newToken).isPresent(), "new token not stored");
        adapter.deleteUser(existingData.getPlayerId());
    }

    @Test
    public void allowNewRefreshTokenForInvalidUserTest(){
        UUID playerId = getInvalidPlayerId();
        SecurityClaims credentials = Player.builder()
                .playerId(playerId)
                .build();
        assertThrows(PlayerNotFoundException.class, ()-> adapter.allowNewRefreshToken(credentials));
    }

    //if more than one argument -> paramtrized type must be Arguments
    private static Stream<NewPlayerInfo> newSingupRequests(
            @Value("${player.username.default}") String defaultUsername){
        return Stream.of(
                Player.asRegistered("bla","xxx"),
                //Player.asAnnonimous() //not working
                Player.builder()
                        //not working
                        // .username(DiceGameContext.getDefaultUsername())
                        .username(defaultUsername)
                        /*
                        ok -> so problem in this method.
                        Due it's an static method, injection in DiceGameContext
                        still it's not done
                         */
                        .security(PlayerSecurity.builder()
                                .role(Role.ANONYMOUS)
                                .build())
                        .build()
        );
    }



    private UUID getInvalidPlayerId(){
        UUID id;
        do {
            id = UUID.randomUUID();

        }while (adapter.existsPlayer(id));
        return id;
    }

    @Test
    public void removeTokenTest(){
        SecurityClaims saved = adapter
                .newPlayerWithRefreshToken(Player.asAnnonimous());
        UUID playerId = saved.getPlayerId();
        UUID token1 = saved.getRefreshTokenId();
        assertTrue(auxiliarSecurityAdapter.existsRefreshToken(token1), "token when persisting not stored");

        adapter.removeRefreshToken(token1);
        assertFalse(auxiliarSecurityAdapter.existsRefreshToken(token1), "token when persisting still stored");
        adapter.deleteUser(playerId);
    }

    @Test
    public void delleteAllTokensByUserTest(){
        SecurityClaims user = adapter
                .newPlayerWithRefreshToken(Player.asAnnonimous());
        UUID token1 = user.getRefreshTokenId();
        UUID token2 = adapter.allowNewRefreshToken(user).getRefreshTokenId();
        UUID token3 = adapter.allowNewRefreshToken(user).getRefreshTokenId();
        Set<UUID> ids = Set.of( token1,token2,token3);//set to assert differents tokens
        for (UUID id : ids){
            assertTrue(auxiliarSecurityAdapter.existsRefreshToken(id),"token is not stored");
        }

        adapter.deleteAllRefreshTokensByUser(user.getPlayerId());
        for (UUID id : ids){
            assertFalse(auxiliarSecurityAdapter.existsRefreshToken(id),"refresh token still exists");
        }
        adapter.deleteUser(user.getPlayerId());
    }
}
