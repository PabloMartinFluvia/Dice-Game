package org.pablomartin.S5T2Dice_Game.security.jwt.providers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PlayerCredentials;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.RefreshTokenPrincipal;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Component("RefreshProvider")
public class RefreshJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider {

    public RefreshJwtAuthenticationProvider(JwtService jwtService, SecurityPersistenceAdapter adapter) {
        super(jwtService, adapter);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        //valid jwt
        if(!jwtService.isValidRefreshJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not a refresh jwt or is corrupted");
        }
        UUID tokenId = jwtService.getTokenIdFromRefreshJwt(jwt);

        //token has not been removed (due logout, reset, etc..)
        if(!adapter.existsRefreshToken(tokenId)){
            throw new JWTVerificationException("This bearer refresh token has been disabled");
        }

        Optional<PlayerCredentials> player = adapter.findOwnerByRefreshTokenId(tokenId);
        if(player.isEmpty()){
            //in case user has been deleted, AFTER providing the refresh jwt
            adapter.invalidateRefreshToken(tokenId);
            throw new JwtAuthenticationException("This bearer refresh token does not belong anymore to any user");
        }else {
            credentials = player.get();
            UUID ownerIdClaimed = jwtService.getUserIdFromRefreshJwt(jwt);
            if(!ownerIdClaimed.equals(credentials.getUserId())){
                //in case, due any reason, user id changed AFTER providing the refresh jwt
                adapter.invalidateRefreshToken(tokenId);
                throw new JWTVerificationException("The bearer refresh token's claim 'subject' it's not the owner of this token. " +
                        "Request a new access JWT.");
            }
        }
    }

    @Override
    protected RefreshTokenPrincipal loadPrincipal(String jwt){
        UUID tokenId = jwtService.getTokenIdFromRefreshJwt(jwt);
        return credentials.toRefreshTokenPrincipal(tokenId);
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadAuthorities() throws JWTVerificationException {
        /*
        in this project one authentication done with a refresh token doesn't require
        store the authorities for extra authorizations filters
         */
        return Collections.EMPTY_SET;
    }
}
