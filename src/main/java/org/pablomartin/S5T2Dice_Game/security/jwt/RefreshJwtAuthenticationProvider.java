package org.pablomartin.S5T2Dice_Game.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.old.PersistenceAdapterV2;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.old.JwtServiceOld;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component("RefreshProvider")
public class RefreshJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider{

    private final PersistenceAdapterV2 persistenceAdapterV2;

    public RefreshJwtAuthenticationProvider(JwtServiceOld jwtServiceOld, PersistenceAdapterV2 persistenceAdapterV2) {
        super(jwtServiceOld);
        this.persistenceAdapterV2 = persistenceAdapterV2;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        //valid jwt
        if(!jwtServiceOld.isValidRefreshJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not a refresh jwt or is corrupted");
        }
        UUID tokenId = jwtServiceOld.getTokenIdFromRefreshJwt(jwt);
        //token still valid
        if(!persistenceAdapterV2.existsRefreshToken(tokenId)){
            throw new JWTVerificationException("This bearer refresh token is no longer valid");
        }
        //CLAIMS IN JWT SYNCRONIZED WITH WHAT IS STORED
        //exists an user who owns this jwt
        Optional<UUID> ownerId = persistenceAdapterV2.findOwnerByRefreshToken(tokenId)
                .map(owner -> owner.getPlayerId());
        if(ownerId.isEmpty()){
            //in case user has been deleted, without invalidating the refresh tokens
            persistenceAdapterV2.deleteRefreshTokenById(tokenId); //invalidate the token
            throw new JWTVerificationException("This bearer refresh token does not belong anymore to any user.");
        }
        //the owner found it's the same that claims the jwt
        UUID actual = ownerId.orElse(null);
        UUID claimed = jwtServiceOld.getUserIdFromRefreshJwt(jwt);
        if(!actual.equals(claimed)){
            //in case, due any reason, user id changed AFTER providing the refresh jwt
            persistenceAdapterV2.deleteRefreshTokenById(tokenId); //invalidate the token
            throw new JWTVerificationException("This bearer refresh token's claim 'subject' it's no longer valid");
        }
    }

    @Override
    protected Object loadPrincipal(String jwt) throws JWTVerificationException {
        UUID tokenId = jwtServiceOld.getTokenIdFromRefreshJwt(jwt);
        UUID userId = jwtServiceOld.getUserIdFromRefreshJwt(jwt);
        //In this project: Principal for RefreshJWT ->
        // Token (refresh token id + Player (only stores player id, rest null))
        PlayerOld owner = PlayerOld.builder().playerId(userId).build(); //owner only has id value, rest null
        return new Token(tokenId, owner);
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadAuthorities(String jwt) throws JWTVerificationException {
        /*
        With an authentication provided with a refresh token bearer the client only can access
        to resoureces available for any authenticated request (without the need to authorize
        in function of the gtanted authorities).
         */
        return Collections.EMPTY_SET;
    }
}
