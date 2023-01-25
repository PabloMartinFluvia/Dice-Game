package org.pablomartin.S5T2Dice_Game.domain.data;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.Utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Log4j2
public class ReposStarter {

    private final PersistenceAdapter persistenceAdapter;

    private final PasswordEncoder encoder;

    private final String ADMIN_PABLO = "Pablo";
    private final String PASSWORD_ADMIN = "1234";

    public ReposStarter(PersistenceAdapter persistenceAdapter, PasswordEncoder encoder) {
        this.persistenceAdapter = persistenceAdapter;
        this.encoder = encoder;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @PostConstruct
    public void init(){
        log.info("----Finding ADMINs-----");
        Collection<Player> admins = persistenceAdapter.findAdmins();
        if(admins.isEmpty()){
            Player admin = Player.builder()
                    .username(ADMIN_PABLO)
                    .password(encoder.encode(PASSWORD_ADMIN))
                    .role(Role.ADMIN)
                    .registerDate(TimeUtils.nowSecsTruncated())
                    .build();
            persistenceAdapter.saveOrUpdate(admin);
            log.info("-----Crated ADMIN------");
        }
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @PreDestroy
    public void finish(){
        log.warn("-------Cleaning repositories-------");
        persistenceAdapter.cleanDB();
        log.warn("----All entities/documents deleted-----");
    }
}
