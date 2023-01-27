package org.pablomartin.S5T2Dice_Game.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component("RefreshProvider")
public class RefreshJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider{

    private final PersistenceAdapter persistenceAdapter;

    public RefreshJwtAuthenticationProvider(JwtService jwtService, PersistenceAdapter persistenceAdapter) {
        super(jwtService);
        this.persistenceAdapter = persistenceAdapter;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        //valid jwt
        if(!jwtService.isValidRefreshJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not a refresh jwt or is corrupted");
        }
        UUID tokenId = jwtService.getTokenIdFromRefreshJwt(jwt);
        //token still valid
        if(!persistenceAdapter.existsRefreshToken(tokenId)){
            throw new JWTVerificationException("This bearer refresh token is no longer valid");
        }
        //CLAIMS IN JWT SYNCRONIZED WITH WHAT IS STORED
        //exists an user who owns this jwt
        Optional<UUID> ownerId = persistenceAdapter.findOwnerByRefreshToken(tokenId)
                .map(owner -> owner.getPlayerId());
        if(ownerId.isEmpty()){
            //in case user has been deleted, without invalidating the refresh tokens
            persistenceAdapter.deleteRefreshTokenById(tokenId); //invalidate the token
            throw new JWTVerificationException("This bearer refresh token does not belong anymore to any user.");
        }
        //the owner found it's the same that claims the jwt
        UUID actual = ownerId.orElse(null);
        UUID claimed = jwtService.getUserIdFromRefreshJwt(jwt);
        if(!actual.equals(claimed)){
            //in case, due any reason, user id changed AFTER providing the refresh jwt
            persistenceAdapter.deleteRefreshTokenById(tokenId); //invalidate the token
            throw new JWTVerificationException("This bearer refresh token's claim 'subject' it's no longer valid");
        }
    }

    @Override
    protected Object loadPrincipal(String jwt) throws JWTVerificationException {
        UUID tokenId = jwtService.getTokenIdFromRefreshJwt(jwt);
        UUID userId = jwtService.getUserIdFromRefreshJwt(jwt);
        //In this project: Principal for RefreshJWT ->
        // Token (refresh token id + Player (only stores player id, rest null))
        Player owner = Player.builder().playerId(userId).build(); //owner only has id value, rest null
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
