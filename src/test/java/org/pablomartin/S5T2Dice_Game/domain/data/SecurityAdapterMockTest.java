package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDocRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenDocRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenEntityRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntityRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.PrincipalProjection;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.PrincipalProjectionFromRefreshToken;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.exceptions.DataSourcesNotSyncronizedException;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)  //magrate the test to Junit5
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
public class SecurityAdapterMockTest {

    @Autowired
    private SecurityPersistenceAdapter adapter;

    private AccessPersistenceAdapter auxiliarAccessAdapter = (AccessPersistenceAdapter) adapter;

    @MockBean
    private PlayerEntityRepository playerEntityRepository;

    @MockBean
    private PlayerDocRepository playerDocRepository;

    @MockBean
    private RefreshTokenEntityRepository tokenEntityRepository;

    @MockBean
    private RefreshTokenDocRepository tokenDocRepository;

    @Test
    public void loadPrincipalByUsernameTest(){
        UUID userId = UUID.randomUUID();
        String username = "blabla";
        String password = "xx";
        Role role = Role.REGISTERED;
        Optional<PrincipalProjection> projection = Optional.of(principalProjectionMock(userId,username,password,role));
        given(playerEntityRepository.findPrincipalProjectionByUsername(username))
                .willReturn(projection);
        given(playerDocRepository.findPrincipalProjectionByUsername(username))
                .willReturn(projection);
        Optional<PrincipalProvider> found = adapter.loadCredentialsByUsername(username);
        doPrincipalAssertions(found, userId, username, password, role);

        //not found
        given(playerEntityRepository.findPrincipalProjectionByUsername(username))
                .willReturn(Optional.empty());
        given(playerDocRepository.findPrincipalProjectionByUsername(username))
                .willReturn(Optional.empty());
        assertTrue(adapter.loadCredentialsByUsername(username).isEmpty(), "principal source optional is not empty");
        //not found in mongo
        given(playerEntityRepository.findPrincipalProjectionByUsername(username))
                .willReturn(projection);
        given(playerDocRepository.findPrincipalProjectionByUsername(username))
                .willReturn(Optional.empty());
        assertThrows(DataSourcesNotSyncronizedException.class,
                () -> adapter.loadCredentialsByUsername(username), "exception is not throwed");
        //not found in sql
        given(playerEntityRepository.findPrincipalProjectionByUsername(username))
                .willReturn(Optional.empty());
        given(playerDocRepository.findPrincipalProjectionByUsername(username))
                .willReturn(projection);
        assertThrows(DataSourcesNotSyncronizedException.class,
                () -> adapter.loadCredentialsByUsername(username), "exception is not throwed");
    }

    @Test
    public void loadPrincipalByIdTest(){
        UUID userId = UUID.randomUUID();
        String username = "blabla";
        String password = "xx";
        Role role = Role.REGISTERED;
        Optional<PrincipalProjection> projection = Optional.of(principalProjectionMock(userId,username,password,role));
        given(playerEntityRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(projection);
        given(playerDocRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(projection);
        Optional<PrincipalProvider> found = adapter.loadCredentialsByUserId(userId);
        doPrincipalAssertions(found, userId, username, password, role);

        //not found
        given(playerEntityRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(Optional.empty());
        given(playerDocRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(Optional.empty());
        assertTrue(adapter.loadCredentialsByUserId(userId).isEmpty(), "principal source optional is not empty");
        //not found in mongo
        given(playerEntityRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(projection);
        given(playerDocRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(Optional.empty());
        assertThrows(DataSourcesNotSyncronizedException.class,
                () -> adapter.loadCredentialsByUserId(userId), "exception is not throwed");
        //not found in sql
        given(playerEntityRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(Optional.empty());
        given(playerDocRepository.findPrincipalProjectionByPlayerId(userId))
                .willReturn(projection);
        assertThrows(DataSourcesNotSyncronizedException.class,
                () -> adapter.loadCredentialsByUserId(userId), "exception is not throwed");
    }

    @Test
    public void loadPrincipalByRefreshTokenTest(){
        UUID userId = UUID.randomUUID();
        String username = "blabla";
        String password = "xx";
        Role role = Role.REGISTERED;
        UUID refreshTokenId = UUID.randomUUID();
        Optional<PrincipalProjectionFromRefreshToken> projection =
                Optional.of(principalProjectionRefreshTokenMock(refreshTokenId,userId,username,password,role));
        given(tokenEntityRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(projection);
        given(tokenDocRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(projection);
        Optional<PrincipalProvider> found = adapter.loadCredentialsByRefreshTokenId(refreshTokenId);
        doPrincipalAssertions(found, userId, username, password, role);

        //not found
        given(tokenEntityRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(Optional.empty());
        given(tokenDocRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(Optional.empty());
        assertTrue(adapter.loadCredentialsByRefreshTokenId(refreshTokenId).isEmpty()
                , "principal source optional is not empty");
        //not found in mongo
        given(tokenEntityRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(projection);
        given(tokenDocRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(Optional.empty());
        assertThrows(DataSourcesNotSyncronizedException.class,
                () -> adapter.loadCredentialsByRefreshTokenId(refreshTokenId)
                , "exception is not throwed");
        //not found in sql
        given(tokenEntityRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(Optional.empty());
        given(tokenDocRepository.findPrincipalProjectionByRefreshTokenId(refreshTokenId))
                .willReturn(projection);
        assertThrows(DataSourcesNotSyncronizedException.class,
                () -> adapter.loadCredentialsByRefreshTokenId(refreshTokenId)
                , "exception is not throwed");
    }

    private void doPrincipalAssertions(Optional<PrincipalProvider> found,
                                       UUID userId,
                                       String username,
                                       String password,
                                       Role role){
        assertTrue(found.isPresent(), "principal source optional is empty");
        PrincipalProvider source = found.orElse(null);
        assertEquals(userId,source.getUserId(),"player id not equals");
        assertEquals(username, source.getUsername(),"username not equals");
        assertEquals(password, source.getPassword(),"password not equals");
        assertEquals(role,source.getUserRole(),"role not equals");
        assertTrue(source.getAuthorities().contains(new SimpleGrantedAuthority(role.withPrefix())),
                "role not stored in authorities");
    }

    private PrincipalProjection principalProjectionMock(UUID playerId, String username, String password, Role role){
        PrincipalProjection.SecurityProjection security = new PrincipalProjection.SecurityProjection() {
            @Override
            public String getPassword() {
                return password;
            }

            @Override
            public Role getRole() {
                return role;
            }
        };

        return new PrincipalProjection() {
            @Override
            public UUID getPlayerId() {
                return playerId;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public SecurityProjection getSecurityDetails() {
                return security;
            }
        };
    }

    private PrincipalProjectionFromRefreshToken principalProjectionRefreshTokenMock
            (UUID refreshTokenId, UUID playerId, String username, String password, Role role){
        return new PrincipalProjectionFromRefreshToken() {
            @Override
            public PrincipalProjection getPlayer() {
                return principalProjectionMock(playerId, username,password,role);
            }
        };
    }

}
