package org.pablomartin.S5T2Dice_Game.domain.data;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.Utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenMongoRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenEntity;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenMySqlRepository;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.EntitiesConverter;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerMongoReposiroty;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerMySqlRepository;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Log4j2
public class DefaultPersistenceAdapter implements PersistenceAdapter{

    //If more datasources will cohexist -> Iterator dessign pattern should be implemented
    // Obs: All repositories must extend the same interface, with the required methods available

    private final PlayerMySqlRepository playerSqlRepo;

    private final PlayerMongoReposiroty playerMongoRepo;

    private final RefreshTokenMySqlRepository refreshTokenSqlRepo;

    private final RefreshTokenMongoRepository refreshTokenMongoRepo;

    private final EntitiesConverter converter; //models to entities/docs and viceversa

    @Override
    public boolean isUsernameRegistered(String username) {
        return (boolean) converter.assertIdenticalObject(
                playerSqlRepo.existsByUsername(username),
                playerMongoRepo.existsByUsername(username));
    }

    @Override
    public Player saveNewPlayer(Player player){

        player.setRegisterDate(TimeUtils.nowSecsTruncated());

        PlayerEntity entity = playerSqlRepo.save(converter.entityFromPlayer(player));
        //using the entity persisted, to make sure idem ID and register date

        if(false){
            //for testing transaction
            throw new RuntimeException("Transaction interrumped");
        }

        PlayerDoc doc = playerMongoRepo.save(converter.docFromEntity(entity));
        //log.info(entity.toString());
        //log.info(doc.toString());
        return converter.assertIdenticalModel(entity,doc);
    }

    @Override
    public Token saveNewRefreshToken(Token refreshToken){
        RefreshTokenEntity entity = refreshTokenSqlRepo.save(converter.entityFromRefreshToken(refreshToken));
        RefreshTokenDoc doc = refreshTokenMongoRepo.save(converter.docFromEntity(entity));
        return converter.assertIdenticalModel(entity,doc);
    }

    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        Optional<PlayerEntity> entity = playerSqlRepo.findByUsername(username);
        Optional<PlayerDoc> doc = playerMongoRepo.findByUsername(username);
        return converter.assertIdenticalOptionalModel(entity,doc);
    }

    @Override
    public Optional<Player> findPlayerById(UUID playerId) {
        Optional<PlayerEntity> entity = playerSqlRepo.findById(playerId);
        Optional<PlayerDoc> doc = playerMongoRepo.findById(playerId);
        return converter.assertIdenticalOptionalModel(entity,doc);
    }

    @Override
    public boolean existsRefreshTokenById(UUID refreshTokenId){
        boolean entity = refreshTokenSqlRepo.existsById(refreshTokenId);
        boolean doc = refreshTokenMongoRepo.existsById(refreshTokenId);
        return (boolean) converter.assertIdenticalObject(entity,doc);
    }

    @Override
    public void deleteRefreshTokenById(UUID refreshTokenId) {
        refreshTokenSqlRepo.deleteById(refreshTokenId);
        refreshTokenMongoRepo.deleteById(refreshTokenId);
        assertRefreshTokenRemoved(refreshTokenId);
    }

    @Override
    public void deleteAllRefreshTokenFromPlayer(UUID playerId) {
        refreshTokenSqlRepo.deleteByOwner_PlayerId(playerId);
        refreshTokenMongoRepo.deleteByOwner_PlayerId(playerId);
        assertPlayerHasNotRefreshTokens(playerId);
    }

    private void assertRefreshTokenRemoved(UUID refreshTokenId) {
        boolean existSql = refreshTokenSqlRepo.existsById(refreshTokenId);
        boolean existMongo = refreshTokenMongoRepo.existsById(refreshTokenId);
        boolean result = (boolean) converter.assertIdenticalObject(existSql,existMongo);
        Assert.isTrue(!result,"This refresh token must no exist!");
    }

    private void assertPlayerHasNotRefreshTokens(UUID playerId) {
        long numberSql = refreshTokenSqlRepo.countByOwner_PlayerId(playerId);
        long numberMongo = refreshTokenMongoRepo.countByOwner_PlayerId(playerId);
        long number = (long) converter.assertIdenticalObject(numberSql,numberMongo);
        Assert.isTrue(number == 0,"Must not exist any refresh token for this player.");
    }


}
