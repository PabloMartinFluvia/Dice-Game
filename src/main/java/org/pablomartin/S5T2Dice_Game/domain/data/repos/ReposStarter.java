package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.Utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerMongoReposiroty;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenMongoRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerMySqlRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenMySqlRepository;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@Log4j2
public class ReposStarter {

    private final PlayerMySqlRepository playerSqlRepo;

    private final PlayerMongoReposiroty playerMongoRepo;

    private final RefreshTokenMySqlRepository refreshTokenSqlRepo;

    private final RefreshTokenMongoRepository refreshTokenMongoRepo;

    private final EntitiesConverter converter; //models to entities/docs and viceversa

    private final PasswordEncoder encoder;

    private final String ADMIN_PABLO = "Pablo";
    private final String PASSWORD_ADMIN = "1234";

    public ReposStarter(PlayerMySqlRepository playerSqlRepo,
                        PlayerMongoReposiroty playerMongoRepo,
                        RefreshTokenMySqlRepository refreshTokenSqlRepo,
                        RefreshTokenMongoRepository refreshTokenMongoRepo,
                        EntitiesConverter converter,
                        PasswordEncoder encoder) {
        this.playerSqlRepo = playerSqlRepo;
        this.playerMongoRepo = playerMongoRepo;
        this.refreshTokenSqlRepo = refreshTokenSqlRepo;
        this.refreshTokenMongoRepo = refreshTokenMongoRepo;
        this.converter = converter;
        this.encoder = encoder;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @PostConstruct
    public void init(){
        log.info("----Finding ADMINs-----");
        Collection<SimplePlayerProjection> admins = (Collection<SimplePlayerProjection>)
                converter.assertIdenticalObject(
                    this.playerSqlRepo.findByRoleIn(List.of(Role.ADMIN), SimplePlayerProjection.class),
                    this.playerMongoRepo.findByRoleIn(List.of(Role.ADMIN), SimplePlayerProjection.class));
        if(admins.isEmpty()){
            Player admin = Player.builder()
                    .username(ADMIN_PABLO)
                    .password(encoder.encode(PASSWORD_ADMIN))
                    .role(Role.ADMIN)
                    .registerDate(TimeUtils.nowSecsTruncated())
                    .build();
            PlayerEntity entity = playerSqlRepo.save(converter.entityFromPlayer(admin));
            //log.info(entity.toString());
            PlayerDoc doc = playerMongoRepo.save(converter.docFromEntity(entity));
            //log.info(doc.toString());
            Player player = converter.assertIdenticalModel(entity,doc);
            log.info("-----Crated ADMIN------");
            //log.info(player.toString());
        }
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @PreDestroy
    public void finish(){
        log.warn("-------Cleaning repositories-------");
        refreshTokenSqlRepo.deleteAll();
        refreshTokenMongoRepo.deleteAll();
        playerSqlRepo.deleteAll();
        playerMongoRepo.deleteAll();
        log.warn("----All entities/documents deleted-----");
    }
}
