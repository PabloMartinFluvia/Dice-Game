package org.pablomartin.S5T2Dice_Game.domain.data.repos;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.RefreshTokenDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.RefreshTokenEntity;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mongo.PlayerDoc;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.mysql.PlayerEntity;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;
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


    /**
     * Used instead of ObjectMapper (due can be problems when parsing time formats)
     * @param source Not null
     * @param typeTarget Requires No args constructor
     * @return
     * @param <T>
     */
    private <T> T copyProperties(@NotNull Object source, Class<T> typeTarget) {
        try {
            T target = typeTarget.getDeclaredConstructor().newInstance();
            BeanUtils.copyProperties(source,target);
            return target;
        }catch (RuntimeException | NoSuchMethodException | InstantiationException | IllegalAccessException |
                InvocationTargetException ex){
            String message = typeTarget + " can't be instantiated with no args constructor";
            log.error("---------"+message+"--------");
            throw new RuntimeException(message);
        }
    }

    /*
    Return the value/object if equals arguments.
    If not DB are not syncronized -> exception
     */
    public <T> T assertEquals(@Nullable T o1, @Nullable T o2){
        if(!Objects.equals(o1,o2)){
            throw DbNotSyncronized(o1, o2);
        }
        return o1; //or o2 <- are equals objects
    }


    //-----------------INPUTS:------------------------

    public PlayerEntity entityFromModel(PlayerOld playerOld){
        //works meanwhile fiels are the same type
        PlayerEntity entity = copyProperties(playerOld,PlayerEntity.class);
        return entity;
    }

    public RefreshTokenEntity entityFromModel(Token refreshToken){
        //works meanwhile fiels are the same type
        RefreshTokenEntity entity = copyProperties(refreshToken, RefreshTokenEntity.class);

        PlayerEntity owner = entityFromModel(refreshToken.getOwner());
        entity.setOwner(owner);
        return entity;
    }

    public PlayerDoc docFromEntity(PlayerEntity entity){
        //works meanwhile fields share same type
        return copyProperties(entity,PlayerDoc.class);
    }

    public RefreshTokenDoc docFromEntity(RefreshTokenEntity entity){
        //works meanwhile fiels are the same type
        RefreshTokenDoc doc = copyProperties(entity, RefreshTokenDoc.class);

        PlayerDoc owner = docFromEntity(entity.getOwner());
        doc.setOwner(owner);
        return doc;
    }



    //-----------------RESULTS:------------------------

    private DataSourcesNotSyncronizedException DbNotSyncronized(Object sql, Object mongo){
        String message = "Datasources are not syncronized. This objects must be equals: \n"+
                "Value MySQL: "+sql+"\n" +
                "Value MongoDB: "+mongo;
        log.error(message);
        return new DataSourcesNotSyncronizedException(message);
    }

    public PlayerOld toModel(PlayerEntity entity, PlayerDoc doc){
        PlayerOld playerOld =  assertEquals(
                //copyProperties works meanwhile fiels are the same type
                copyProperties(entity, PlayerOld.class),
                copyProperties(doc, PlayerOld.class));
        return playerOld;
    }

    public Token toModel(RefreshTokenEntity entity, RefreshTokenDoc doc){
        //works meanwhile fiels are the same type
        Token fromEntity = copyProperties(entity, Token.class);
        fromEntity.setOwner(copyProperties(entity.getOwner(), PlayerOld.class));

        Token fromDoc = copyProperties(doc, Token.class);
        fromDoc.setOwner(copyProperties(doc.getOwner(), PlayerOld.class));

        return assertEquals(fromEntity,fromDoc);
    }

    public Optional<PlayerOld> toOptionalModel(Optional<PlayerEntity> entity, Optional<PlayerDoc> doc){
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

    public Collection<PlayerOld> toModelCollection(Collection<PlayerEntity> entities, Collection<PlayerDoc> docs){
        int size = assertEquals(entities.size(), docs.size());
        Collection<PlayerOld> result = new LinkedHashSet<>(size);
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
