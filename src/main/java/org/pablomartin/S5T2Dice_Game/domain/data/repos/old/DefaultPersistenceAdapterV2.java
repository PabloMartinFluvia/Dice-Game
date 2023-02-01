package org.pablomartin.S5T2Dice_Game.domain.data.repos.old;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenMongoRepository;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenEntity;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenMySqlRepository;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.EntitiesConverter;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerMongoReposiroty;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerMySqlRepository;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@RequiredArgsConstructor
@Log4j2
public class DefaultPersistenceAdapterV2 implements PersistenceAdapterV2 {

    //If more datasources will cohexist -> Iterator dessign pattern should be implemented
    // Obs: All repositories must extend the same interface, with the required methods available

    private final PlayerMySqlRepository playerSqlRepo;

    private final PlayerMongoReposiroty playerMongoRepo;

    private final RefreshTokenMySqlRepository refreshTokenSqlRepo;

    private final RefreshTokenMongoRepository refreshTokenMongoRepo;

    private final EntitiesConverter converter; //models to entities/docs and viceversa

    @Override
    public boolean existsPlayer(UUID playerId) {
        return converter.assertEquals(
                playerSqlRepo.existsById(playerId),
                playerMongoRepo.existsById(playerId));
    }

    @Override
    public boolean isUsernameRegistered(String username) {
        return converter.assertEquals(
                playerSqlRepo.existsByUsername(username),
                playerMongoRepo.existsByUsername(username));
    }

    @Override
    public boolean existsRefreshToken(UUID refreshTokenId){
        return converter.assertEquals(
                refreshTokenSqlRepo.existsById(refreshTokenId),
                refreshTokenMongoRepo.existsById(refreshTokenId));
    }

    @Override
    public PlayerOld saveOrUpdate(PlayerOld playerOld) {
        PlayerEntity entity = playerSqlRepo.save(converter.entityFromModel(playerOld));

        //using the entity persisted, to assert ID in mongo equals ID generated for entity
        PlayerDoc doc = playerMongoRepo.save(converter.docFromEntity(entity));

        return converter.toModel(entity,doc);
    }

    @Override
    public Token saveOrUpdate(Token refreshToken){
        RefreshTokenEntity entity = refreshTokenSqlRepo.save(converter.entityFromModel(refreshToken));

        //using the entity persisted, to assert ID in mongo equals ID generated for entity
        RefreshTokenDoc doc = refreshTokenMongoRepo.save(converter.docFromEntity(entity));

        return converter.toModel(entity,doc);
    }

    @Override
    public Optional<PlayerOld> findPlayerById(UUID playerId) {
        Optional<PlayerEntity> entity = playerSqlRepo.findById(playerId);
        Optional<PlayerDoc> doc = playerMongoRepo.findById(playerId);
        return converter.toOptionalModel(entity,doc);
    }

    @Override
    public Optional<PlayerOld> findPlayerByUsername(String username) {
        Optional<PlayerEntity> entity = playerSqlRepo.findByUsername(username);
        Optional<PlayerDoc> doc = playerMongoRepo.findByUsername(username);
        return converter.toOptionalModel(entity,doc);
    }

    @Override
    public Optional<PlayerOld> findOwnerByRefreshToken(UUID tokenId) {
        Optional<PlayerEntity> sql = this.refreshTokenSqlRepo.findById(tokenId)
                .map(token -> token.getOwner());
        Optional<PlayerDoc> mongo = this.refreshTokenMongoRepo.findById(tokenId)
                .map(token -> token.getOwner());
        return converter.toOptionalModel(sql,mongo);
    }

    @Override
    public Collection<PlayerOld> findAdmins() {
        Collection<PlayerEntity> sql = this.playerSqlRepo
                .findByRoleIn(List.of(Role.ADMIN));
        Collection<PlayerDoc> mongo = this.playerMongoRepo
                .findByRoleIn(List.of(Role.ADMIN));
        return converter.toModelCollection(sql,mongo);
    }

    @Override
    public void deleteRefreshTokenById(UUID refreshTokenId) {
        refreshTokenSqlRepo.deleteById(refreshTokenId);
        refreshTokenMongoRepo.deleteById(refreshTokenId);
        assertRefreshTokenRemoved(refreshTokenId);
    }

    private void assertRefreshTokenRemoved(UUID refreshTokenId) {
        boolean existSql = refreshTokenSqlRepo.existsById(refreshTokenId);
        boolean existMongo = refreshTokenMongoRepo.existsById(refreshTokenId);
        boolean exists = converter.assertEquals(existSql,existMongo);
        if(exists){
            //force rollback in transaction
            throw new RuntimeException("This refresh token must no exist: "+refreshTokenId);
        }
    }

    @Override
    public void deleteAllRefreshTokenFromPlayer(UUID ownerId) {
        refreshTokenSqlRepo.deleteByOwner_PlayerId(ownerId);
        refreshTokenMongoRepo.deleteByOwner_PlayerId(ownerId);
        assertPlayerHasNotRefreshTokens(ownerId);
    }

    private void assertPlayerHasNotRefreshTokens(UUID playerId) {
        long countSql = refreshTokenSqlRepo.countByOwner_PlayerId(playerId);
        long countMongo = refreshTokenMongoRepo.countByOwner_PlayerId(playerId);
        long count = converter.assertEquals(countSql,countMongo);
        if(count != 0){
            //force rollback in transaction
            throw new RuntimeException("Must not exist any refresh token for this player: "+playerId);
        }
    }

    /*
    Order matters, due mapped relations could not be bidireccional
     */
    @Override
    public void cleanDB(){
        deleteAllRefreshTokens();
        deleteAllPlayers();
    }

    private void deleteAllPlayers() {
        playerSqlRepo.deleteAll();
        playerMongoRepo.deleteAll();
    }

    private void deleteAllRefreshTokens() {
        refreshTokenSqlRepo.deleteAll();
        refreshTokenMongoRepo.deleteAll();
    }
}
