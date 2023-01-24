package org.pablomartin.S5T2Dice_Game.domain.data;

import com.auth0.jwt.exceptions.JWTVerificationException;
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
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.*;

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
    public boolean existsPlayerById(UUID playerId) {
        return converter.assertIdenticalObject(
                playerSqlRepo.existsById(playerId),
                playerMongoRepo.existsById(playerId));
    }

    @Override
    public boolean isUsernameRegistered(String username) {
        return converter.assertIdenticalObject(
                playerSqlRepo.existsByUsername(username),
                playerMongoRepo.existsByUsername(username));
    }

    @Override
    public boolean existsRefreshTokenById(UUID refreshTokenId){
        boolean entity = refreshTokenSqlRepo.existsById(refreshTokenId);
        boolean doc = refreshTokenMongoRepo.existsById(refreshTokenId);
        return converter.assertIdenticalObject(entity,doc);
    }

    @Override
    public Player saveNewPlayer(Player player){
        player.setRegisterDate(TimeUtils.nowSecsTruncated());
        return savePlayer(player);
    }

    @Override
    public Player savePlayer(Player player) {
        PlayerEntity entity = playerSqlRepo.save(converter.entityFromModel(player));
        //using the entity persisted, to make sure idem ID and register date
        PlayerDoc doc = playerMongoRepo.save(converter.docFromEntity(entity));
        return converter.toModel(entity,doc);
    }

    @Override
    public Token saveNewRefreshToken(Token refreshToken){
        RefreshTokenEntity entity = refreshTokenSqlRepo.save(converter.entityFromModel(refreshToken));
        //using the entity persisted, to make sure idem ID
        RefreshTokenDoc doc = refreshTokenMongoRepo.save(converter.docFromEntity(entity));
        return converter.toModel(entity,doc);
    }

    @Override
    public Optional<Player> findPlayerById(UUID playerId) {
        Optional<PlayerEntity> entity = playerSqlRepo.findById(playerId);
        Optional<PlayerDoc> doc = playerMongoRepo.findById(playerId);
        return converter.toOptionalModel(entity,doc);
    }

    @Override
    public Optional<Player> findPlayerByUsername(String username) {
        Optional<PlayerEntity> entity = playerSqlRepo.findByUsername(username);
        Optional<PlayerDoc> doc = playerMongoRepo.findByUsername(username);
        return converter.toOptionalModel(entity,doc);
    }

    @Override
    public Collection<Player> findAdmins() {
        Collection<PlayerEntity> sql = this.playerSqlRepo
                .findByRoleIn(List.of(Role.ADMIN));
        Collection<PlayerDoc> mongo = this.playerMongoRepo
                .findByRoleIn(List.of(Role.ADMIN));
        return converter.toModelCollection(sql,mongo);
    }

    @Override
    public Optional<UUID> findOwnerIdByRefreshTokenId(UUID tokenId) {
        Optional<UUID> sql = this.refreshTokenSqlRepo.findById(tokenId)
                .map(token -> token.getOwner().getPlayerId());
        Optional<UUID> mongo = this.refreshTokenMongoRepo.findById(tokenId)
                .map(token -> token.getOwner().getPlayerId());
        return converter.toOptionalObject(sql, mongo);
    }

    @Override
    public Optional<Role> findPlayerRole(UUID ownerId) {
        return findPlayerById(ownerId).map(player -> player.getRole());
    }

    @Override
    public void deleteRefreshTokenById(UUID refreshTokenId) {
        refreshTokenSqlRepo.deleteById(refreshTokenId);
        refreshTokenMongoRepo.deleteById(refreshTokenId);
        assertRefreshTokenRemoved(refreshTokenId);
    }

    @Override
    public void deleteAllPlayers() {
        playerSqlRepo.deleteAll();
        playerMongoRepo.deleteAll();
        assertNoPlayers();
    }

    @Override
    public void deleteAllRefreshTokens() {
        refreshTokenSqlRepo.deleteAll();
        refreshTokenMongoRepo.deleteAll();
        asserNoTokens();
    }


    @Override
    public void deleteAllRefreshTokenFromPlayer(Player player) {
        UUID playerId = player.getPlayerId();
        refreshTokenSqlRepo.deleteByOwner_PlayerId(playerId);
        refreshTokenMongoRepo.deleteByOwner_PlayerId(playerId);
        assertPlayerHasNotRefreshTokens(playerId);
    }

    private void assertNoPlayers(){
        long numberSql = playerSqlRepo.count();
        long numberMongo = playerMongoRepo.count();
        long number = converter.assertIdenticalObject(numberSql,numberMongo);
        Assert.isTrue(number == 0,"Must not exist any refresh token for this player.");
    }

    private void asserNoTokens(){
        long numberSql = refreshTokenSqlRepo.count();
        long numberMongo = refreshTokenMongoRepo.count();
        long number = converter.assertIdenticalObject(numberSql,numberMongo);
        Assert.isTrue(number == 0,"Must not exist any refresh token.");
    }

    private void assertRefreshTokenRemoved(UUID refreshTokenId) {
        boolean existSql = refreshTokenSqlRepo.existsById(refreshTokenId);
        boolean existMongo = refreshTokenMongoRepo.existsById(refreshTokenId);
        boolean result = converter.assertIdenticalObject(existSql,existMongo);
        Assert.isTrue(!result,"This refresh token must no exist!");
    }

    private void assertPlayerHasNotRefreshTokens(UUID playerId) {
        long numberSql = refreshTokenSqlRepo.countByOwner_PlayerId(playerId);
        long numberMongo = refreshTokenMongoRepo.countByOwner_PlayerId(playerId);
        long number = converter.assertIdenticalObject(numberSql,numberMongo);
        Assert.isTrue(number == 0,"Must not exist any refresh token for this player.");
    }
}
