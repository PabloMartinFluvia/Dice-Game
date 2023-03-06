package org.pablomartin.S5T2Dice_Game.domain.data.repos;

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
            -> el més senzill i el que més s'assemblaria a la implementació en mongo (@DBRef o @DocumentReference)

          -----> De moment @ManyToOne
         */

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.data.SettingsPersistenceAdapter;
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
import org.pablomartin.S5T2Dice_Game.exceptions.DataSourcesNotSynchronizedException;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Log4j2
public class DefaultPersistenceAdapter implements SettingsPersistenceAdapter, GamePersistenceAdapter, SecurityPersistenceAdapter, AdminPersistenceAdapter {

    private final PlayerEntityRepository playerEntityRepository;
    private final PlayerDocRepository playerDocRepository;
    private final RefreshTokenEntityRepository refreshTokenEntityRepository;
    private final RefreshTokenDocRepository refreshTokenDocRepository;
    private final RollEntityRepository rollEntityRepository;
    private final RollDocRepository rollDocRepository;

    private static final String NOT_NULL_DATA = "credentials must not be null";
    private static final String NOT_NULL_PLAYER_ID = "player id must be not null";


    //CREATE

    @Override //integration test
    public SecurityClaims newPlayerWithRefreshToken(@NotNull NewPlayerInfo details) {
        Assert.notNull(details, NOT_NULL_DATA);

        LocalDateTime now = TimeUtils.nowSecsTruncated();
        PlayerEntity playerEntity = playerEntityRepository
                .save(PlayerEntity.of(details, now));
        PlayerDoc playerDoc =playerDocRepository
                .save(PlayerDoc.of(playerEntity.getPlayerId(),details,now));
        return allowNewRefreshToken(playerEntity, playerDoc);
    }

    private SecurityClaims allowNewRefreshToken(
            @NotNull PlayerEntity playerEntity,
            @NotNull PlayerDoc playerDoc){
        Assert.isTrue(playerEntity != null && playerDoc !=null, "player should not be null");

        RefreshTokenEntity refreshTokenEntity = refreshTokenEntityRepository
                .save(RefreshTokenEntity.of(playerEntity));
        UUID refreshTokenId = refreshTokenEntity.getRefreshTokenId();

        RefreshTokenDoc refreshTokenDoc = refreshTokenDocRepository
                .save(RefreshTokenDoc.of(refreshTokenId, playerDoc));

        SecurityClaims modelFromSql = refreshTokenEntity.toCredentialsForJWT();
        SecurityClaims modelFromMongo = refreshTokenDoc.toCredentialsForJWT();

        return checkEquals(modelFromSql,modelFromMongo);
    }

    @Override //integration test
    public SecurityClaims allowNewRefreshToken(@NotNull SecurityClaims claims) throws PlayerNotFoundException{
        Assert.notNull(claims, NOT_NULL_DATA);

        PlayerEntity playerEntity = playerEntityRepository
                .findById(claims.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(claims.getPlayerId()));
        PlayerDoc playerDoc = playerDocRepository
                .findById(claims.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(claims.getPlayerId()));
        return allowNewRefreshToken(playerEntity, playerDoc);
    }

    @Override //integration test
    public RollDetails saveRoll(@NotNull UUID playerId, @NotNull RollDetails roll) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        Assert.notEmpty(Collections.singletonList(roll.getDicesValues()),"roll must contain dices");

        PlayerEntity playerEntity = playerEntityRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        PlayerDoc playerDoc = playerDocRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        LocalDateTime now = TimeUtils.nowSecsTruncated();
        RollEntity rollEntity = rollEntityRepository.save(RollEntity.of(roll, playerEntity,now));
        RollDoc rollDoc = rollDocRepository.save(RollDoc.of(roll, rollEntity.getRollId(), playerDoc, now));

        return checkEquals(rollEntity.toRollDetails(),rollDoc.toRollDetails());
    }

    //READ

    @Override //integration
    public boolean existsPlayer(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        Boolean sqlResult = playerEntityRepository.existsById(playerId);
        Boolean mongoResult = playerDocRepository.existsById(playerId);
        return checkEquals(sqlResult,mongoResult);
    }

    @Override //tested with integration and mocks
    public boolean isUsernameAvailable(@NotNull String username) {
        Assert.notNull(username, "username must be not null");
        Boolean sqlResult = !playerEntityRepository.existsByUsername(username);
        Boolean mongoResult = !playerDocRepository.existsByUsername(username);
        return checkEquals(sqlResult,mongoResult);
    }

    @Override //tested with integration
    public boolean existsRefreshToken(@NotNull UUID refreshTokenId) {
        Assert.notNull(refreshTokenId, "refresh token id must be not null");
        Boolean sqlResult = refreshTokenEntityRepository.existsById(refreshTokenId);
        Boolean mongoResult = refreshTokenDocRepository.existsById(refreshTokenId);
        return checkEquals(sqlResult,mongoResult);
    }

    @Override //integration test
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public Optional<GameDetails> findGame(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        Optional<UsernameAndId> playerEntity = playerEntityRepository.findUsernameByPlayerId(playerId);
        Optional<UsernameAndId> playerDoc = playerDocRepository.findUsernameByPlayerId(playerId);
        List<RollDetails> rolls = findAllRolls(playerId);
        Optional<GameDetails> modelFromSql = playerEntity
                .map(projection -> projection.toGameDetails(rolls));
        Optional<GameDetails> modelFromMongo = playerDoc
                .map(projection -> projection.toGameDetails(rolls));
        return checkOptionals(modelFromSql,modelFromMongo);
    }

    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public List<GameDetails> findAllGames() {
        List<UsernameAndId> playersEntities = playerEntityRepository.findUsernameBy();
        List<UsernameAndId> playersDocs = playerDocRepository.findUsernameBy();
        //for compare the 2 lists: comparator used for sorting uses the id
        List<UsernameAndId> players =
                checkLists(playersEntities,playersDocs,Comparator.comparing(UsernameAndId::getPlayerId));
        return players.stream()
                .map(player -> player.toGameDetails(findAllRolls(player.getPlayerId())))
                .collect(Collectors.toList()); //if more control (or assert mutable): https://www.logicbig.com/tutorials/core-java-tutorial/java-util-stream/collect.html
    }

    @Override // integration test
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public List<RollDetails> findAllRolls(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);

        List<RollDetails> rollsEntities = rollEntityRepository
                .findByPlayer_PlayerId(playerId)
                .map(RollWithoutPlayerProjection::toRollDetails)
                .collect(Collectors.toList());
        List<RollDetails> rollsDocs = rollDocRepository
                .findByPlayer_PlayerId(playerId)
                .map(RollWithoutPlayerProjection::toRollDetails)
                .collect(Collectors.toList());

        //for compare the 2 lists: comparator used for sorting uses the date (and the id if there's a tie)
        return checkLists(rollsEntities,rollsDocs, RollDetails::compareDate);
    }


    //https://stackoverflow.com/questions/47258103/mock-projection-result-spring-data-jpa
    @Override //integration and with mocks
    public Optional<PrincipalProvider> loadCredentialsByUserId(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        Optional<PrincipalProvider> modelFromSql = playerEntityRepository
                .findPrincipalProjectionByPlayerId(playerId)
                .map(PrincipalProjection::toPrincipalProvider);
        Optional<PrincipalProvider> modelFromMongo = playerDocRepository
                .findPrincipalProjectionByPlayerId(playerId)
                .map(PrincipalProjection::toPrincipalProvider);
        return checkOptionals(modelFromSql,modelFromMongo);
    }

    @Override //integration and with mocks
    public Optional<PrincipalProvider> loadCredentialsByUsername(@NotNull String username) {
        Assert.notNull(username, "username must be not null");
        Optional<PrincipalProvider> modelFromSql = playerEntityRepository
                .findPrincipalProjectionByUsername(username)
                .map(PrincipalProjection::toPrincipalProvider);
        Optional<PrincipalProvider> modelFromMongo = playerDocRepository
                .findPrincipalProjectionByUsername(username)
                .map(PrincipalProjection::toPrincipalProvider);
        return checkOptionals(modelFromSql,modelFromMongo);
    }

    @Override //integration and with mocks
    public Optional<PrincipalProvider> loadCredentialsByRefreshTokenId(@NotNull UUID tokenId) {
        Assert.notNull(tokenId, "token id must be not null");

        Optional<PrincipalProvider> modelFromSql = refreshTokenEntityRepository
                .findPrincipalProjectionByRefreshTokenId(tokenId)
                .map(PrincipalProjectionFromRefreshToken::toPrincipalProvider);
        Optional<PrincipalProvider> modelFromMongo = refreshTokenDocRepository
                .findPrincipalProjectionByRefreshTokenId(tokenId)
                .map(PrincipalProjectionFromRefreshToken::toPrincipalProvider);
        return checkOptionals(modelFromSql,modelFromMongo);
    }

    @Override //integration and with mocks
    public Optional<Role> findUserRole(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        Optional<Role> modelFromSql = playerEntityRepository
                .findRoleProjectionByPlayerId(playerId)
                .map(projection -> projection.getSecurityDetails().getRole());
        Optional<Role> modelFromMongo = playerDocRepository
                .findRoleProjectionByPlayerId(playerId)
                .map(projection -> projection.getSecurityDetails().getRole());
        return checkOptionals(modelFromSql,modelFromMongo);
    }

    @Override //integration and mocks
    public SecurityClaims updateCredentials(@NotNull NewPlayerInfo details) {
        Assert.notNull(details, NOT_NULL_DATA);
        Assert.isTrue(details.getPlayerAuthenticatedId().isPresent(), "player id must be provided");
        Assert.isTrue(details.getRole().equals(Role.REGISTERED),"instance must have set Role Registered");

        UUID playerId = details.getPlayerAuthenticatedId().orElse(UUID.randomUUID()); //id is present due Assert, orElse(not null) to skip sonar warnings
        PlayerEntity updatableEntity = playerEntityRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));
        PlayerDoc updatableDoc = playerDocRepository.findById(playerId)
                .orElseThrow(() -> new PlayerNotFoundException(playerId));

        String newUsername = details.getUsername();
        if(newUsername != null){
            updatableEntity.setUsername(newUsername);
            updatableDoc.setUsername(newUsername);
        }

        String newPassword = details.getPasswordEncoded();
        if(newPassword != null){
            updatableEntity.getSecurityDetails().setPassword(newPassword);
            updatableDoc.getSecurityDetails().setPassword(newPassword);
        }

        if(newUsername != null && newPassword != null){
            updatableEntity.getSecurityDetails().setRole(Role.REGISTERED);
            updatableDoc.getSecurityDetails().setRole(Role.REGISTERED);
        }

        SecurityClaims modelFromSql = playerEntityRepository.save(updatableEntity)
                .toCredentialsForAccessJWT();
        SecurityClaims modelFromMongo = playerDocRepository.save(updatableDoc)
                .toCredentialsForAccessJWT();

        return checkEquals(modelFromSql,modelFromMongo);
    }

    //delete

    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public void deleteUser(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        deleteAllRolls(playerId);
        deleteAllRefreshTokensByUser(playerId);
        playerEntityRepository.deleteById(playerId);
        playerDocRepository.deleteById(playerId);
    }


    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public void deleteAllRolls(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        rollEntityRepository.deleteByPlayer_PlayerId(playerId);
        rollDocRepository.deleteByPlayer_PlayerId(playerId);
    }

    @Override //integration
    public void removeRefreshToken(@NotNull UUID refreshTokenId) {
        Assert.notNull(refreshTokenId, "refresh token id must be not null");
        refreshTokenEntityRepository.deleteById(refreshTokenId);
        refreshTokenDocRepository.deleteById(refreshTokenId);
    }

    @Override //integration
    @Transactional(transactionManager = "chainedTransactionManager") //if not the test can't execute
    public void deleteAllRefreshTokensByUser(@NotNull UUID playerId) {
        Assert.notNull(playerId, NOT_NULL_PLAYER_ID);
        refreshTokenEntityRepository.deleteByPlayer_PlayerId(playerId);
        refreshTokenDocRepository.deleteByPlayer_PlayerId(playerId);
    }

    //STARTER

    @Override //integration and mocks
    public boolean existsAdmin(String adminName) {

         Optional<Role> modelFromSql =playerEntityRepository
                 .findPrincipalProjectionByUsername(adminName)
                 .map(projection -> projection.getSecurityDetails().getRole());

        Optional<Role> modelFromMongo =playerDocRepository
                .findPrincipalProjectionByUsername(adminName)
                .map(projection -> projection.getSecurityDetails().getRole());

        Optional<Role> role = checkOptionals(modelFromSql,modelFromMongo);
        return role.map(value -> value.equals(Role.ADMIN)).orElse(false);
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

    private <T> T checkEquals(@Nullable T sql, @Nullable T  mongo) {
        if(Objects.equals(sql,mongo)){
            return sql; //can be any
        }else{
            throw dbsNotSynchronized(sql,mongo);
        }
    }

    private <T> Optional<T> checkOptionals(Optional<T> sql, Optional<T> mongo){
        if(sql.isEmpty() && mongo.isEmpty()){
            return Optional.empty();
        }else {
            return Optional.ofNullable(checkEquals(sql.orElse(null),mongo.orElse(null)));
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
            throw dbsNotSynchronized(sql,mongo);
        }
    }

    private DataSourcesNotSynchronizedException dbsNotSynchronized(Object sql, Object mongo){
        String message = "Datasources are not synchronized. This objects must be equals: \n"+
                "Value MySQL: "+sql+"\n" +
                "Value MongoDB: "+mongo;
        log.error(message);
        return new DataSourcesNotSynchronizedException(message);
    }
}
