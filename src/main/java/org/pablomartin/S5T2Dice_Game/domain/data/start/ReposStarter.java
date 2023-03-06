package org.pablomartin.S5T2Dice_Game.domain.data.start;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.PlayerSecurity;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@PropertySource("classpath:values.properties")
@Log4j2
public class ReposStarter {

    private final AdminPersistenceAdapter adapter;

    private final PasswordEncoder encoder;

    @Value("${admin.username}")
    private String USERNAME_ADMIN;

    @Value("${admin.password}")
    private String PASSWORD_ADMIN;

    @Value("${admin.deleteAll}")
    private boolean CLEAN_DB;

    @Transactional(transactionManager = "chainedTransactionManager")
    @PostConstruct
    public void init(){
        log.info("----Finding ADMIN-----");
        if(!adapter.existsAdmin(USERNAME_ADMIN)){
            NewPlayerInfo admin = Player.builder()
                    .username(USERNAME_ADMIN)
                    .security(PlayerSecurity.builder()
                            .passwordEncoded(encoder.encode(PASSWORD_ADMIN))
                            .role(Role.ADMIN)
                            .build())
                    .build();
            adapter.newPlayerWithRefreshToken(admin);
            //this username never will be available for login, due saved in @PostConstruct
            log.info("-----ADMIN created------");
        }else {
            log.info("-----ADMIN found------");
        }
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @PreDestroy
    public void finish(){
        if(CLEAN_DB){
            log.warn("-------Cleaning repositories-------");
            adapter.cleanDB();
            log.warn("----All entities/documents deleted-----");
        }
    }
}
