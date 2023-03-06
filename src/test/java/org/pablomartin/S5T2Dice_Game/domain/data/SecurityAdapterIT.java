package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.pablomartin.S5T2Dice_Game.domain.models.*;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)  //magrate the test to Junit5
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
public class SecurityAdapterIT {

    @Autowired
    private SecurityPersistenceAdapter adapter;

    @Autowired
    private SettingsPersistenceAdapter auxiliarAccessAdapter;

    @ParameterizedTest
    @MethodSource("newSingupRequests")
    public void loadPrincipalByUsernameTest(String username, String passwordEncoded, Role role){
        UUID userId = saveUser(username,passwordEncoded,role);
        Optional<PrincipalProvider> pricnipalSource =
                adapter.loadCredentialsByUsername(username);
        doPrincipalProviderAssertions(pricnipalSource, userId, username, passwordEncoded, role);
        auxiliarAccessAdapter.deleteUser(userId);
    }

    @ParameterizedTest
    @MethodSource("newSingupRequests")
    public void loadPrincipalByIdTest(String username, String passwordEncoded, Role role){
        UUID userId = saveUser(username,passwordEncoded,role);
        Optional<PrincipalProvider> pricnipalSource =
                adapter.loadCredentialsByUserId(userId);
        doPrincipalProviderAssertions(pricnipalSource, userId, username, passwordEncoded, role);
        auxiliarAccessAdapter.deleteUser(userId);
    }

    @ParameterizedTest
    @MethodSource("newSingupRequests")
    public void loadPrincipalByRefreshTokenTest(String username, String passwordEncoded, Role role){
        SecurityClaims user = saveUserWithRefreshToken(username,passwordEncoded,role);
        UUID userId = user.getPlayerId();
        UUID tokenId = user.getRefreshTokenId();
        Optional<PrincipalProvider> pricnipalSource =
                adapter.loadCredentialsByRefreshTokenId(tokenId);
        doPrincipalProviderAssertions(pricnipalSource, userId, username, passwordEncoded, role);
        auxiliarAccessAdapter.deleteUser(userId);
    }

    @Test
    public void existsRefreshTokenTest(){
        SecurityClaims user = saveUserWithRefreshToken("A","B",null);
        UUID userId = user.getPlayerId();
        UUID token1 = user.getRefreshTokenId();
        UUID token2 = auxiliarAccessAdapter.allowNewRefreshToken(user).getRefreshTokenId();
        assertTrue(adapter.existsRefreshToken(token1),"token not saved when user persisted");
        assertTrue(adapter.existsRefreshToken(token2),"token not saved when new token added to user");
        auxiliarAccessAdapter.deleteUser(userId);
    }

    //@Test //test disbled. Implementation shared with AccessPersistenceAdapter. Tested in AccessAdapterIT
    public void removeRefreshTokenTest(){
        SecurityClaims saved = auxiliarAccessAdapter
                .newPlayerWithRefreshToken(Player.asVisitor());
        UUID playerId = saved.getPlayerId();
        UUID token1 = saved.getRefreshTokenId();
        assertTrue(adapter.existsRefreshToken(token1), "token when persisting not stored");

        adapter.removeRefreshToken(token1);
        assertFalse(adapter.existsRefreshToken(token1), "token when persisting still stored");
        auxiliarAccessAdapter.deleteUser(playerId);
    }

    private UUID saveUser(String username, String passwordEncoded, Role role){
        NewPlayerInfo player = buildCredentials(username, passwordEncoded, role);
        return auxiliarAccessAdapter.newPlayerWithRefreshToken(player).getPlayerId();
    }

    private SecurityClaims saveUserWithRefreshToken(String username, String passwordEncoded, Role role){
        NewPlayerInfo player = buildCredentials(username, passwordEncoded, role);
        return auxiliarAccessAdapter.newPlayerWithRefreshToken(player);
    }

    private void doPrincipalProviderAssertions(Optional<PrincipalProvider> pricnipalSource,
                                               UUID userId,
                                               String username,
                                               String passwordEncoded,
                                               Role role){
        assertTrue(pricnipalSource.isPresent(),"data soruce not loaded.");
        PrincipalProvider source = pricnipalSource.orElse(null);
        assertEquals(userId,source.getUserId(),"player id not equals");
        assertEquals(username, source.getUsername(),"username not equals");
        assertEquals(passwordEncoded, source.getPassword(),"password not equals");
        assertEquals(role,source.getUserRole(),"role not equals");
        assertTrue(source.getAuthorities().contains(new SimpleGrantedAuthority(role.withPrefix())),
                "role not stored in authorities");
    }

    //if more than one argument -> paramtrized type must be Arguments
    private static Stream<Arguments> newSingupRequests(
            @Value("${player.username.default}") String defaultUsername){
        return Stream.of(
                Arguments.of("bla","xxx",Role.REGISTERED),
                Arguments.of(defaultUsername,null,Role.VISITOR));
    }

    private NewPlayerInfo buildCredentials(String username, String passwordEncoded, Role role){
        return Player.builder()
                //id not provided
                .username(username)
                .security(PlayerSecurity.builder() //at least one field not null
                        .passwordEncoded(passwordEncoded)
                        .role(role)
                        //refresh token id not provided
                        .build())
                .build();
    }
}
