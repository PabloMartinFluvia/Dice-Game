package org.pablomartin.S5T2Dice_Game.domain.data.start;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.PlayerSecurity;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReposStarter {

    private final AdminAdapter adapter;

    private final PasswordEncoder encoder;

    private final String ADMIN_PABLO = "Pablo";
    private final String PASSWORD_ADMIN = "1234";

    @Transactional(transactionManager = "chainedTransactionManager")
    @PostConstruct
    public void init(){
        log.info("----Finding ADMIN-----");
        if(!adapter.existsAdmin(ADMIN_PABLO)){
            NewPlayerInfo admin = Player.builder()
                    .username(ADMIN_PABLO)
                    .security(PlayerSecurity.builder()
                            .passwordEncoded(encoder.encode(PASSWORD_ADMIN))
                            .role(Role.ADMIN)
                            .build())
                    .build();
            adapter.newPlayerWithRefreshToken(admin);
            //this username never will be available for login, due saved in @PostConstruct
            log.info("-----ADMIN created------");
        }
        log.info("-----ADMIN found------");
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    //@PreDestroy
    public void finish(){
        log.warn("-------Cleaning repositories-------");
        adapter.cleanDB();
        log.warn("----All entities/documents deleted-----");
    }
}
