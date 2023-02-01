package org.pablomartin.S5T2Dice_Game.domain.data.repos.old;

import jakarta.annotation.PostConstruct;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.Utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;

@Service
@Log4j2
public class ReposStarter {

    private final PersistenceAdapterV2 persistenceAdapterV2;

    private final PasswordEncoder encoder;

    private final String ADMIN_PABLO = "Pablo";
    private final String PASSWORD_ADMIN = "1234";

    public ReposStarter(PersistenceAdapterV2 persistenceAdapterV2, PasswordEncoder encoder) {
        this.persistenceAdapterV2 = persistenceAdapterV2;
        this.encoder = encoder;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @PostConstruct
    public void init(){
        log.info("----Finding ADMINs-----");
        Collection<PlayerOld> admins = persistenceAdapterV2.findAdmins();
        if(admins.isEmpty()){
            PlayerOld admin = PlayerOld.builder()
                    .username(ADMIN_PABLO)
                    .password(encoder.encode(PASSWORD_ADMIN))
                    .role(Role.ADMIN)
                    .registerDate(TimeUtils.nowSecsTruncated())
                    .build();
            persistenceAdapterV2.saveOrUpdate(admin);
            log.info("-----Crated ADMIN------");
        }
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    //@PreDestroy
    public void finish(){
        log.warn("-------Cleaning repositories-------");
        persistenceAdapterV2.cleanDB();
        log.warn("----All entities/documents deleted-----");
    }
}
