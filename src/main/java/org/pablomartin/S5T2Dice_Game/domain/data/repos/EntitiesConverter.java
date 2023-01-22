package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenEntity;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;
import org.pablomartin.S5T2Dice_Game.domain.models.RefreshToken;
import org.pablomartin.S5T2Dice_Game.exceptions.DataSourcesNotSyncronizedException;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

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

    public Object assertIdenticalObject(Object sql, Object mongo){
        if(!Objects.equals(sql,mongo)){
            throw DbNotSyncronized(sql, mongo);
        }
        return sql; //or mongo <- are equals objects
    }

    public Player assertIdenticalModel(PlayerEntity entity, PlayerDoc doc){
        Player player =  (Player) assertIdenticalObject(
                parseType(entity,Player.class),
                parseType(doc,Player.class));
        return player;
    }

    public RefreshToken assertIdenticalModel(RefreshTokenEntity entity, RefreshTokenDoc doc){
        Player owner = assertIdenticalModel(entity.getOwner(),doc.getOwner()); //player entity vs player doc
        UUID tokenId = (UUID) assertIdenticalObject(entity.getTokenId(),doc.getTokenId());
        return new RefreshToken(tokenId,owner);
    }

    public Optional<Player> assertIdenticalOptionalModel(Optional<PlayerEntity> entity, Optional<PlayerDoc> doc){
        if(entity.isPresent() && doc.isPresent()) {
            Optional<Player> player = Optional.of((Player) assertIdenticalObject(
                    parseType(entity.get(), Player.class),
                    parseType(doc.get(), Player.class)));
            return player;
        }else if(entity.isEmpty() && doc.isEmpty()){
            //in both DB not found
            return Optional.empty();
        }else{
            //found only in one DB
            throw DbNotSyncronized(entity,doc);
        }
    }

    private DataSourcesNotSyncronizedException DbNotSyncronized(Object sql, Object mongo){
        String message = "Datasources are not syncronized. This objects must be equals: \n"+
                "Value MySQL: "+sql+"\n" +
                "Value MongoDB: "+mongo;
        log.error(message);
        return new DataSourcesNotSyncronizedException(message);
    }

    public PlayerEntity entityFromPlayer(Player player){
        PlayerEntity entity = parseType(player,PlayerEntity.class);
        return entity;
    }

    public RefreshTokenEntity entityFromRefreshToken(RefreshToken refreshToken){
        return new RefreshTokenEntity(parseType(refreshToken.getOwner(),PlayerEntity.class));
    }

    public PlayerDoc docFromEntity(PlayerEntity entity){
        //works meanwhile fields share same type
        return parseType(entity,PlayerDoc.class);
    }

    public RefreshTokenDoc docFromEntity(RefreshTokenEntity entity){
        RefreshTokenDoc doc = parseType(entity, RefreshTokenDoc.class);
        //owner field are different types, only parsed id values
        doc.setOwner(docFromEntity(entity.getOwner()));
        return doc;
    }






}
