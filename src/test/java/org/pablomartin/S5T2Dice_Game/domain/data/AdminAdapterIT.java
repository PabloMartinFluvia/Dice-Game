package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pablomartin.S5T2Dice_Game.domain.data.start.AdminPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)  //magrate the test to Junit5
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
public class AdminAdapterIT {

    @Autowired
    private AdminPersistenceAdapter adapter;

    @Autowired
    private GamePersistenceAdapter auxiliarGameAdapter;

    @Autowired
    private SecurityPersistenceAdapter auxiliarSecurityAdapter;

    @Test
    public void existsAdminTest(){
        String adminName = "ad";
        assertFalse(adapter.existsAdmin(adminName),"admin with this username found");
        UUID id = adapter.newPlayerWithRefreshToken(Player.asRegistered(adminName,null)).getPlayerId();
        assertFalse(adapter.existsAdmin(adminName), "admin role registered found as admin");
        adapter.deleteUser(id);
        NewPlayerInfo admin = Player.builder()
                .username(adminName)
                .security(PlayerSecurity.builder()
                        .role(Role.ADMIN)
                        .build())
                .build();
        id = adapter.newPlayerWithRefreshToken(admin).getPlayerId();
        assertTrue(adapter.existsAdmin(adminName), "admin not found");
        adapter.deleteUser(id);
    }

    @Test //be careful with this test, will DELETE ALL in DB
    public void cleanDBTest(){
        SecurityClaims userA = adapter.newPlayerWithRefreshToken(Player.asRegistered(null,null));
        SecurityClaims userB = adapter.newPlayerWithRefreshToken(Player.asVisitor());
        UUID idA = userA.getPlayerId();
        UUID idB = userB.getPlayerId();
        UUID tokenA1 = userA.getRefreshTokenId();
        UUID tokenB1 = userB.getRefreshTokenId();
        UUID tokenA2 = adapter.allowNewRefreshToken(userA).getRefreshTokenId();
        UUID tokenB2 = adapter.allowNewRefreshToken(userB).getRefreshTokenId();
        auxiliarGameAdapter.saveRoll(idA, new Roll(new int[]{1,2}));
        auxiliarGameAdapter.saveRoll(idA, new Roll(new int[]{3,4}));
        auxiliarGameAdapter.saveRoll(idB, new Roll(new int[]{6,6}));
        adapter.cleanDB();
        assertFalse(adapter.existsPlayer(idA),"user A exists");
        assertFalse(adapter.existsPlayer(idB),"user B exists");
        assertFalse(auxiliarSecurityAdapter.existsRefreshToken(tokenA1),"token a1 exists");
        assertFalse(auxiliarSecurityAdapter.existsRefreshToken(tokenA2),"token a2 exists");
        assertFalse(auxiliarSecurityAdapter.existsRefreshToken(tokenB1),"token b1 exists");
        assertFalse(auxiliarSecurityAdapter.existsRefreshToken(tokenB2),"token b2 exists");
        assertTrue(auxiliarGameAdapter.findAllRolls(idA).isEmpty(),"userA's rolls found");
        assertTrue(auxiliarGameAdapter.findAllRolls(idB).isEmpty(),"userB's rolls found");
    }
}
