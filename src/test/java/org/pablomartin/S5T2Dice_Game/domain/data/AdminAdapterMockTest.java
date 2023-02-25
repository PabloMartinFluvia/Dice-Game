package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDocRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntityRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.PrincipalProjection;
import org.pablomartin.S5T2Dice_Game.domain.data.start.AdminPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)  //magrate the test to Junit5
@SpringBootTest
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
public class AdminAdapterMockTest {

    @Autowired
    private AdminPersistenceAdapter adapter;

    @MockBean
    private PlayerEntityRepository playerEntityRepository;

    @MockBean
    private PlayerDocRepository playerDocRepository;

    @Test
    public void existsAdminTest(){
        String adminName = "ad";
        confifMocks(adminName, Optional.empty());
        assertFalse(adapter.existsAdmin(adminName),"admin found for an inexistent username");

        confifMocks(adminName, projectionOf(adminName, Role.ANONYMOUS));
        assertFalse(adapter.existsAdmin(adminName),"user with role anonymous found as admin");

        confifMocks(adminName, projectionOf(adminName, Role.REGISTERED));
        assertFalse(adapter.existsAdmin(adminName),"user with role registered found as admin");

        confifMocks(adminName, projectionOf(adminName, Role.ADMIN));
        assertTrue(adapter.existsAdmin(adminName),"admin not found");
    }

    private void confifMocks(String adminName, Optional<PrincipalProjection> projection){
        given(playerEntityRepository.findPrincipalProjectionByUsername(adminName)).willReturn(projection);
        given(playerDocRepository.findPrincipalProjectionByUsername(adminName)).willReturn(projection);
    }

    private Optional<PrincipalProjection> projectionOf(String username, Role role){
        PrincipalProjection.SecurityProjection security = new PrincipalProjection.SecurityProjection() {

            @Override
            public String getPassword() {
                return null;
            }

            @Override
            public Role getRole() {
                return role;
            }
        };
        return Optional.of(new PrincipalProjection(){


            @Override
            public UUID getPlayerId() {
                return null;
            }

            @Override
            public String getUsername() {
                return username;
            }

            @Override
            public SecurityProjection getSecurityDetails() {
                return security;
            }
        });
    }
}
