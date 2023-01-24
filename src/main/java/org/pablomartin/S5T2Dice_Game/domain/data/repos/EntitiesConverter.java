package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenEntity;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.exceptions.DataSourcesNotSyncronizedException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

@Component
@Log4j2
@AllArgsConstructor
public class EntitiesConverter {

    private final ObjectMapper mapper;

    private <T> T parseType(@NotNull Object source, Class<T> typeTarget) {
        try {

            T target = typeTarget.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source,target);
            return target;
        }catch (RuntimeException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException ex){
            String message = typeTarget + " can't be instantiated";
            log.error("---------"+message+"--------");
            throw new RuntimeException(message);
        }
    }

    public PlayerEntity entityFromModel(Player player){
        PlayerEntity entity = parseType(player,PlayerEntity.class);
        return entity;
    }

    public RefreshTokenEntity entityFromModel(Token refreshToken){
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setTokenId(refreshToken.getTokenId());
        entity.setOwner(entityFromModel(refreshToken.getOwner()));
        return entity;
    }

    public PlayerDoc docFromEntity(PlayerEntity entity){
        //works meanwhile fields share same type
        return parseType(entity,PlayerDoc.class);
    }

    public RefreshTokenDoc docFromEntity(RefreshTokenEntity entity){
        RefreshTokenDoc doc = new RefreshTokenDoc();
        doc.setTokenId(entity.getTokenId());
        doc.setOwner(docFromEntity(entity.getOwner()));
        return doc;
    }



    public <T> T assertIdenticalObject(T sql, T mongo){
        if(!Objects.equals(sql,mongo)){
            throw DbNotSyncronized(sql, mongo);
        }
        return sql; //or mongo <- are equals objects
    }

    private DataSourcesNotSyncronizedException DbNotSyncronized(Object sql, Object mongo){
        String message = "Datasources are not syncronized. This objects must be equals: \n"+
                "Value MySQL: "+sql+"\n" +
                "Value MongoDB: "+mongo;
        log.error(message);
        return new DataSourcesNotSyncronizedException(message);
    }

    public Player toModel(PlayerEntity entity, PlayerDoc doc){
        Player player =  assertIdenticalObject(
                parseType(entity,Player.class),
                parseType(doc,Player.class));
        return player;
    }

    public Token toModel(RefreshTokenEntity entity, RefreshTokenDoc doc){
        Player owner = toModel(entity.getOwner(),doc.getOwner()); //player entity vs player doc
        UUID tokenId = assertIdenticalObject(entity.getTokenId(),doc.getTokenId());
        return new Token(tokenId,owner);
    }

    public Optional<Player> toOptionalModel(Optional<PlayerEntity> entity, Optional<PlayerDoc> doc){
        if(entity.isPresent() && doc.isPresent()) {
            return Optional.of(toModel(entity.get(),doc.get()));
        }else if(entity.isEmpty() && doc.isEmpty()){
            //in both DB not found
            return Optional.empty();
        }else{
            //found only in one DB
            throw DbNotSyncronized(entity,doc);
        }
    }

    public Optional<UUID> toOptionalObject(Optional<UUID> sql, Optional<UUID> mongo) {
        if(sql.isPresent() && mongo.isPresent()) {
            return Optional.of(assertIdenticalObject(sql.get(),mongo.get()));
        }else if(sql.isEmpty() && mongo.isEmpty()){
            //in both DB not found
            return Optional.empty();
        }else{
            //found only in one DB
            throw DbNotSyncronized(sql,mongo);
        }
    }

    public Collection<Player> toModelCollection(Collection<PlayerEntity> entities, Collection<PlayerDoc> docs){
        int size = assertIdenticalObject(entities.size(), docs.size());
        Collection<Player> result = new LinkedHashSet<>(size);
        if(size != 0){
            Iterator<PlayerEntity> sqlIt = entities.iterator();
            Iterator<PlayerDoc> mongoIt = docs.iterator();
            while(sqlIt.hasNext() && mongoIt.hasNext()){
                result.add(toModel(sqlIt.next(),mongoIt.next()));
            }
            Assert.isTrue(!sqlIt.hasNext() && !mongoIt.hasNext(),"Must not be other elements to check.");
        }
        return result;
    }


}
