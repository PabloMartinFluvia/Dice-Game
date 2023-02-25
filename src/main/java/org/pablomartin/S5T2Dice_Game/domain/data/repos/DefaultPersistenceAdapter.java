package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.Utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.data.AccessPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.data.GamePersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.RollWithoutPlayerProjection;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.UsernameAndId;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.*;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.*;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.PrincipalProjection;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.projections.PrincipalProjectionFromRefreshToken;
import org.pablomartin.S5T2Dice_Game.domain.data.start.AdminPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.GameDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.exceptions.DataSourcesNotSyncronizedException;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class DefaultPersistenceAdapter implements AccessPersistenceAdapter, GamePersistenceAdapter, SecurityPersistenceAdapter, AdminPersistenceAdapter {

    private final PlayerEntityRepository playerEntityRepository;
    private final PlayerDocRepository playerDocRepository;
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;
    private final RefreshTokenDocRepository refreshTokenDocRepository;

    private final RollEntityRepository rollEntityRepository;
    private final RollDocRepository rollDocRepository;


    private <T> T checkEquals(T sql, T  mongo) {
        if(Objects.equals(sql,mongo)){
            return sql; //can be any
        }else{
            throw dbsNotSyncronized(sql,mongo);
        }
    }

    private <T> Optional<T> checkOptionals(Optional<T> sql, Optional<T> mongo){
        if(sql.isEmpty() && mongo.isEmpty()){
            return Optional.empty();
        }else {
            return Optional.of(checkEquals(sql.orElse(null),mongo.orElse(null)));
        }
    }

    private <T> List<T> checkLists(List<T> sql, List<T> mongo, Comparator<T> comparator){
        if(sql.isEmpty() && mongo.isEmpty()){
            return new ArrayList<>();
        } else if (sql.size() == mongo.size()) {
            //to assert elements ordered in the same way
            sql.sort(comparator);
            mongo.sort(comparator);
            List<T> result = new ArrayList<>();
            for(int n = 0; n<sql.size(); n++){
                result.add(checkEquals(sql.get(n), mongo.get(n)));
            }
            return result;
        }else {
            throw dbsNotSyncronized(sql,mongo);
        }
    }
    //todo: merge
    private DataSourcesNotSyncronizedException dbsNotSyncronized(Object sql, Object mongo){
        String message = "Datasources are not syncronized. This objects must be equals: \n"+
                "Value MySQL: "+sql+"\n" +
                "Value MongoDB: "+mongo;
        log.error(message);
        return new DataSourcesNotSyncronizedException(message);
    }

    //CREATE

    /**
     * Goal:
     * Persist new entity with the data provided + instant registry.
     * And associate it with a new valid refresh token id.
     *
     * @param credentials : provides username (maybe default),
     *                            password (null or encoded),
     *                            role already setted
     * @return playerId + username + role + refresh token id
     */

    @Override //integration test
    public SecurityClaims newPlayerWithRefreshToken(@NotNull NewPlayerInfo credentials) {
        /*
        Player 1:1 SecurityDetails
            -------> De moment embebed
         SecurityDetails 1:N RefreshToken

         @OneToMany unidireccional -> owner side player (o securityDetails si l'acavo fent @oneToOne)
            -> no m'interessa, associar un nou refresh token al player implica carregar tota la col·lecció
         @OneToMany bidireccional -> navegable des dels 2 cantons (owner el refresh token)
            -> pot arrivar a ser interessant, però més complex
            -> implementar-ho si els pros compensen el cost
          @ManyToOne (unidireccional) -> owner side refresh token
            -> el més senzill i el que més s'assemblaria a l'implementació en mongo (@DBRef o @DocumentReference)

          -----> De moment @ManyToOne
         */

        Assert.notNull(credentials, "credentials must not be null");

        LocalDateTime now = TimeUtils.nowSecsTruncated();

        PlayerEntity playerEntity = playerEntityRepository
                .save(PlayerEntity.of(credentials, now));
        PlayerDoc playerDoc =playerDocRepository
                .save(PlayerDoc.of(playerEntity.getPlayerId(),credentials,now));
        return allowNewRefreshToken(playerEntity, playerDoc);
    }

    // Logic allows null values, invocations must avoid that
    // not directly tested, validated with integration tests when invoked
    private SecurityClaims allowNewRefreshToken(
            @NotNull PlayerEntity playerEntity,
            @NotNull PlayerDoc playerDoc){
        Assert.isTrue(playerEntity != null && playerDoc !=null, "player should not be null");

        RefreshTokenEntity refreshTokenEntity = refreshTokenEntityRepository
                .save(RefreshTokenEntity.of(playerEntity));
        UUID refreshTokenId = refreshTokenEntity.getRefreshTokenId();

        RefreshTokenDoc refreshTokenDoc = refreshTokenDocRepository
                .save(RefreshTokenDoc.of(refreshTokenId,playerDoc));

        SecurityClaims modelFromSql = refreshTokenEntity.toCredentialsForJWT();
        SecurityClaims modelFromMongo = refreshTokenDoc.toCredentialsForJWT();

        return checkEquals(modelFromSql,modelFromMongo);
    }

    /**
     * Goal:
     * Allow a new refresh token linked to te user specified in credentials
     * and returns the credentials.
     *
     * @param credentials
     * @return the param, updated with the new refresh token id.
     * @throws PlayerNotFoundException if the player associated to credentials is not found.
     */
    @Override //integration test
    public SecurityClaims allowNewRefreshToken(@NotNull SecurityClaims credentials)
            throws RuntimeException{
        Assert.notNull(credentials, "credentials must not be null");

        PlayerEntity playerEntity = playerEntityRepository.findById(credentials.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(credentials.getPlayerId()));
        PlayerDoc playerDoc = playerDocRepository.findById(credentials.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(credentials.getPlayerId()));
        return allowNewRefreshToken(playerEntity, playerDoc);
    }

    /**
     * Goal:
     * Save the roll (linked to the target player).
     * Note: if BD schema accepts it, also increments +1 the number of
     * rolls of the target player.
     *
     * @param playerId
     * @param roll
     * @return RollDetails with de dices + instant of roll
     * Note: doesn't contain info if it's a winner or not
     */
    @Override //integration test
    public RollDetails saveRoll(@NotNull UUID playerId, @NotNull RollDetails roll) {
        Assert.notNull(playerId, "player id must be not null");
        Assert.notEmpty(Arrays.asList(roll.getDicesValues()),"roll must contain dices");

        LocalDateTime now = TimeUtils.nowSecsTruncated();
        PlayerEntity playerEntity = playerEntityRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        RollEntity rollEntity = RollEntity.of(playerEntity,roll,now);
        rollEntity = rollEntityRepository.save(rollEntity);
        UUID rollId = rollEntity.getRollId();
        PlayerDoc playerDoc = playerDocRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        RollDoc rollDoc = RollDoc.of(rollId, roll, playerDoc, now);
        rollDoc = rollDocRepository.save(rollDoc);

        return checkEquals(rollEntity.toRollDetails(),rollDoc.toRollDetails());
    }

    //READ

    @Override //integration
    public boolean existsPlayer(@NotNull UUID playerId) {
        Assert.notNull(playerId, "player id must be not null");
        Boolean sql = playerEntityRepository.existsById(playerId);
        Boolean mongo = playerDocRepository.existsById(playerId);
        return checkEquals(sql,mongo);
    }

    @Override //tested with integration and mocks
    public boolean isUsernameAvailable(@NotNull String username) {
        Assert.notNull(username, "username must be not null");
        Boolean sql = !playerEntityRepository.existsByUsername(username);
        Boolean mongo = !playerDocRepository.existsByUsername(username);
        return checkEquals(sql,mongo);
    }

    @Override //tested with integration
    public boolean existsRefreshToken(@NotNull UUID refreshTokenId) {
        Assert.notNull(refreshTokenId, "refresh token id must be not null");
        Boolean sql = refreshTokenEntityRepository.existsById(refreshTokenId);
        Boolean mongo = refreshTokenDocRepository.existsById(refreshTokenId);
        return checkEquals(sql,mongo);
    }

    /**
     * Goal:
     * Populates the fields that can be read directly (username, id, rolls collection).
     *
     * @param playerId
     * @return Optional empty if player not found
     */
    @Override //integration test
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public Optional<GameDetails> findPlayer(@NotNull UUID playerId) {
        Assert.notNull(playerId, "player id must be not null");
        Optional<UsernameAndId> playerSql = playerEntityRepository.findUsernameByPlayerId(playerId);
        Optional<UsernameAndId> playerMongo = playerDocRepository.findUsernameByPlayerId(playerId);
        List<RollDetails> rolls = findAllRolls(playerId);
        Optional<GameDetails> sql = playerSql
                .map(projection -> projection.toPlayerDetails(rolls));
        Optional<GameDetails> mongo = playerMongo
                .map(projection -> projection.toPlayerDetails(rolls));
        return checkOptionals(sql,mongo);
    }

    /**
     * Goal:
     * Provide all players, only with fields that can be read directly (username, id, rolls collection).
     *
     * @return
     */
    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public List<GameDetails> findAllPlayers() {
        List<UsernameAndId> playerSql = playerEntityRepository.findUsernameBy();
        List<UsernameAndId> playerMongo = playerDocRepository.findUsernameBy();
        List<UsernameAndId> players =
                checkLists(playerSql,playerMongo,Comparator.comparing(UsernameAndId::getPlayerId));
        return players.stream()
                .map(player -> player.toPlayerDetails(findAllRolls(player.getPlayerId())))
                .collect(Collectors.toList()); //if more control (or assert mutable): https://www.logicbig.com/tutorials/core-java-tutorial/java-util-stream/collect.html
    }

    /**
     * Goal:
     * Find all rolls (linked to the target id).
     *
     * @param playerId
     * @return collection of RollDetails (can be empty).
     * Each element contain dices + instant of roll. But
     * doesn't contain info if it's a winner or not
     */
    @Override // integration test
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public List<RollDetails> findAllRolls(@NotNull UUID playerId) {
        Assert.notNull(playerId, "player id must be not null");
        //if not found empty list

        List<RollDetails> rollsSql = rollEntityRepository.findByPlayer_PlayerId(playerId)
                .map(RollWithoutPlayerProjection::toRollDetails)
                //.toList()//unmodifiableList
                .collect(Collectors.toList()); //if more control (or assert mutable): https://www.logicbig.com/tutorials/core-java-tutorial/java-util-stream/collect.html
        List<RollDetails> rollsMongo = rollDocRepository.findByPlayer_PlayerId(playerId)
                .map(RollWithoutPlayerProjection::toRollDetails)
                .collect(Collectors.toList());
        return checkLists(rollsSql,rollsMongo, RollDetails::compareDate);
    }




    //https://stackoverflow.com/questions/47258103/mock-projection-result-spring-data-jpa
    @Override //integration and with mocks
    public Optional<PrincipalProvider> loadCredentialsByUserId(@NotNull UUID userId) {
        Assert.notNull(userId, "user id must be not null");
        Optional<PrincipalProvider> sql = playerEntityRepository.findPrincipalProjectionByPlayerId(userId)
                .map(PrincipalProjection::toPrincipalProvider);
        Optional<PrincipalProvider> mongo = playerDocRepository.findPrincipalProjectionByPlayerId(userId)
                .map(PrincipalProjection::toPrincipalProvider);
        return checkOptionals(sql,mongo);
    }




    /**
     * Goal: provide all the possible credentials for authentications
     *
     * @param username
     * @return if present: id + username + password + collection of granted authorities
     * Note: at least one simple granted authority "ROLE_XXX"
     */
    @Override //integration and with mocks
    public Optional<PrincipalProvider> loadCredentialsByUsername(@NotNull String username) {
        Assert.notNull(username, "username must be not null");
        Optional<PrincipalProvider> sql = playerEntityRepository.findPrincipalProjectionByUsername(username)
                .map(PrincipalProjection::toPrincipalProvider);
        Optional<PrincipalProvider> mongo = playerDocRepository.findPrincipalProjectionByUsername(username)
                .map(PrincipalProjection::toPrincipalProvider);
        return checkOptionals(sql,mongo);
    }

    @Override //integration and with mocks
    public Optional<PrincipalProvider> loadCredentialsByRefreshTokenId(@NotNull UUID tokenId) {
        Assert.notNull(tokenId, "token id must be not null");

        Optional<PrincipalProvider> sql = refreshTokenEntityRepository
                .findPrincipalProjectionByRefreshTokenId(tokenId)
                .map(PrincipalProjectionFromRefreshToken::toPrincipalProvider);
        Optional<PrincipalProvider> mongo = refreshTokenDocRepository
                .findPrincipalProjectionByRefreshTokenId(tokenId)
                .map(PrincipalProjectionFromRefreshToken::toPrincipalProvider);
        return checkOptionals(sql,mongo);
    }

    /**
     * Goal: load the role of this user.
     *
     * @param userId
     * @return Empty optional if not found.
     */
    @Override //integration and with mocks
    public Optional<Role> findUserRole(@NotNull UUID userId) {
        Assert.notNull(userId, "user id must be not null");
        Optional<Role> sql = playerEntityRepository.findRoleProjectionByPlayerId(userId)
                .map(projection -> projection.getSecurityDetails().getRole());
        Optional<Role> mongo = playerDocRepository.findRoleProjectionByPlayerId(userId)
                .map(projection -> projection.getSecurityDetails().getRole());
        return checkOptionals(sql,mongo);
    }

    /**
     * Goal:
     * Update the username and password (if not null values) by the player id.
     * Also, update the role to registered if the player was anonymous.
     *
     * @param credentials
     * @return
     */
    @Override //integration and mocks
    public SecurityClaims updateCredentials(@NotNull NewPlayerInfo credentials) {
        Assert.notNull(credentials, "credentials must not be null");
        Assert.isTrue(credentials.getPlayerAuthenticatedId().isPresent(), "player id must be provided");
        Assert.isTrue(credentials.getRole().equals(Role.REGISTERED),"instance must have setted Role Registered");

        UUID playerId = credentials.getPlayerAuthenticatedId().get();
        PlayerEntity entity = playerEntityRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        PlayerDoc doc = playerDocRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        String newUsername = credentials.getUsername();
        if(newUsername != null){
            entity.setUsername(newUsername);
            doc.setUsername(newUsername);
        }

        String newPassword = credentials.getPasswordEncoded();
        if(newPassword != null){
            entity.getSecurityDetails().setPassword(newPassword);
            doc.getSecurityDetails().setPassword(newPassword);
        }

        if(newUsername != null && newPassword != null){
            entity.getSecurityDetails().setRole(Role.REGISTERED);
            doc.getSecurityDetails().setRole(Role.REGISTERED);
        }
        entity = playerEntityRepository.save(entity);
        SecurityClaims sqlModel = entity.toCredentialsForAccessJWT();
        SecurityClaims mongoModel = playerDocRepository.save(doc).toCredentialsForAccessJWT();

        return checkEquals(sqlModel,mongoModel);
    }

    //delete

    /**
     * Goal: remove all info (details + linked) related to the specified user.
     *
     * @param userId
     */
    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public void deleteUser(@NotNull UUID userId) {
        Assert.notNull(userId, "player id must be not null");
        deleteAllRolls(userId);
        deleteAllRefreshTokensByUser(userId);
        playerEntityRepository.deleteById(userId);
        playerDocRepository.deleteById(userId);
    }


    /**
     * Goal:
     * Assert the target player exists.
     * Remove all rolls linked to the target player.
     * Note: also remove/reset all related data IF STORED,(in mongo?)
     * like winrate, num of rolls...
     *
     * @param playerId
     */
    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public void deleteAllRolls(@NotNull UUID playerId) {
        Assert.notNull(playerId, "player id must be not null");
        rollEntityRepository.deleteByPlayer_PlayerId(playerId);
        rollDocRepository.deleteByPlayer_PlayerId(playerId);
    }

    /**
     * Goal:
     * Invalidate the refresh token that matches the provided id.
     *
     * @param refreshTokenId
     */
    @Override //integration
    public void removeRefreshToken(@NotNull UUID refreshTokenId) {
        Assert.notNull(refreshTokenId, "refresh token id must be not null");
        refreshTokenEntityRepository.deleteById(refreshTokenId);
        refreshTokenDocRepository.deleteById(refreshTokenId);
    }

    /**
     * Goal:
     * Invalidate all the refresh tokens linked to the specified user.
     *
     * @param playerId
     */
    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public void deleteAllRefreshTokensByUser(@NotNull UUID playerId) {
        Assert.notNull(playerId, "player id must be not null");
        refreshTokenEntityRepository.deleteByPlayer_PlayerId(playerId);
        refreshTokenDocRepository.deleteByPlayer_PlayerId(playerId);
    }


    @Override //integration and mocks
    public boolean existsAdmin(String adminName) {

         Optional<Role> sql =playerEntityRepository
                 .findPrincipalProjectionByUsername(adminName)
                 .map(projection -> projection.getSecurityDetails().getRole());

        Optional<Role> mongo =playerDocRepository
                .findPrincipalProjectionByUsername(adminName)
                .map(projection -> projection.getSecurityDetails().getRole());

        Optional<Role> role = checkOptionals(sql,mongo);
        if(role.isPresent()){
            return role.get().equals(Role.ADMIN);
        }else{
            return false;
        }
    }

    @Override //integration
    public void cleanDB(){
        refreshTokenEntityRepository.deleteAll();
        refreshTokenDocRepository.deleteAll();
        rollEntityRepository.deleteAll();
        rollDocRepository.deleteAll();
        playerEntityRepository.deleteAll();
        playerDocRepository.deleteAll();
    }
}
