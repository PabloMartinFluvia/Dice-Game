package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDocRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntityRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.OnlyRole;
import org.pablomartin.S5T2Dice_Game.domain.data.start.ReposStarter;
import org.pablomartin.S5T2Dice_Game.domain.models.*;
import org.pablomartin.S5T2Dice_Game.exceptions.DataSourcesNotSynchronizedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

//@RunWith(SpringRunner.class) // runner for JUnit4 -> creates problems with JUnit5 annotations
@ExtendWith(SpringExtension.class)  //magrate the test to Junit5
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
public class AccessAdapterMockTest {

    @Autowired
    private AccessPersistenceAdapter adapter;

    @Autowired
    private SecurityPersistenceAdapter auxiliarAdapter;

    /*
    Mocks:
    https://www.baeldung.com/java-spring-mockito-mock-mockbean
    https://reflectoring.io/spring-boot-mock/
    https://www.baeldung.com/mockito-series

    Both do the same, but:
        when ... then for methods
            when(playerEntityRepository.existsById(playerId)).thenReturn(true);
        given ... will for external responses (like BD calls)
            given(playerEntityRepository.existsById(playerId)).willReturn(true);
     */

    @MockBean
    private PlayerEntityRepository playerEntityRepository;

    @MockBean
    private PlayerDocRepository playerDocRepository;

    @MockBean
    private ReposStarter starter;
    //This bean has @PostConstuct and @PreDestroy -> mock it to ignore the calls to another beans

    @Test
    public void isUsernameAvailableTest(){
        String username = "blabla";
        given(playerEntityRepository.existsByUsername(username)).willReturn(false);
        given(playerDocRepository.existsByUsername(username)).willReturn(false);
        assertTrue(adapter.isUsernameAvailable(username),"username is not available");

        username = "bleble";
        given(playerEntityRepository.existsByUsername(username)).willReturn(true);
        given(playerDocRepository.existsByUsername(username)).willReturn(true);
        assertFalse(adapter.isUsernameAvailable(username),"username is available");
    }

    @ParameterizedTest
    @MethodSource("newSingupRequests") //if name not provided -> JUnit will find a private static method with same name
    public void updateCredentialsTest( NewPlayerInfo old){
        UUID playerId = UUID.randomUUID();

        LocalDateTime now = LocalDateTime.now();
        PlayerEntity oldEntity = PlayerEntity.of(old, now);
        oldEntity.setPlayerId(playerId);
        PlayerDoc oldDdoc = PlayerDoc.of(playerId,old, now);
        given(playerEntityRepository.findById(playerId)).willReturn(Optional.of(oldEntity));
        given(playerDocRepository.findById(playerId)).willReturn(Optional.of(oldDdoc));

        String username = "test";
        String password = "xxx";
        NewPlayerInfo newInfo = Player.asRegistered(username,password);
        newInfo.setPlayerId(playerId);
        PlayerEntity entity = PlayerEntity.of(newInfo,now);
        entity.setPlayerId(playerId);
        PlayerDoc doc = PlayerDoc.of(playerId,newInfo,now);
        given(playerEntityRepository.save(entity)).willReturn(entity);
        given(playerDocRepository.save(doc)).willReturn(doc);
        SecurityClaims result = adapter.updateCredentials(newInfo);

        assertEquals(username,result.getUsername(), "username has not been updated");
        assertEquals(Role.REGISTERED, result.getRole(),"role has not been updated");
        //on update password is not stored in object returned
        //tested if updated in integration test
    }

    @Test
    public void finRoleTest(){
        UUID playerId = UUID.randomUUID();
        Role role = Role.REGISTERED;
        Optional<OnlyRole> projection = Optional.of(onlyRoleProjection(role));

        given(playerEntityRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(projection);
        given(playerDocRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(projection);
        assertTrue(adapter.findUserRole(playerId).isPresent(), "principal source optional is empty");
        assertEquals(role,adapter.findUserRole(playerId).get(), "roles don't match");

        given(playerEntityRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(Optional.empty());
        given(playerDocRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(Optional.empty());
        assertTrue(adapter.findUserRole(playerId).isEmpty(), "principal source optional is not empty");

        given(playerEntityRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(projection);
        given(playerDocRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(Optional.empty());
        assertThrows(DataSourcesNotSynchronizedException.class,
                () -> adapter.findUserRole(playerId), "exception is not throwed");

        given(playerEntityRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(Optional.empty());
        given(playerDocRepository.findRoleProjectionByPlayerId(playerId))
                .willReturn(projection);
        assertThrows(DataSourcesNotSynchronizedException.class,
                () -> adapter.findUserRole(playerId), "exception is not throwed");
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
                                .role(Role.VISITOR)
                                .build())
                        .build()
        );
    }

    private OnlyRole onlyRoleProjection(Role role){
        OnlyRole.RoleSecurity security = new OnlyRole.RoleSecurity() {
            @Override
            public Role getRole() {
                return role;
            }
        };

        return new OnlyRole(){
            @Override
            public RoleSecurity getSecurityDetails() {
                return security;
            }
        };
    }
}
