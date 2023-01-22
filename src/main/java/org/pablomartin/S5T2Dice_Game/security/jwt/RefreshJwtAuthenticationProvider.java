package org.pablomartin.S5T2Dice_Game.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

@Component("RefreshProvider")
public class RefreshJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider{

    private final PersistenceAdapter persistenceAdapter;

    public RefreshJwtAuthenticationProvider(JwtService jwtService, PersistenceAdapter persistenceAdapter) {
        super(jwtService);
        this.persistenceAdapter = persistenceAdapter;
    }

    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        if(!jwtService.isValidRefreshJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not a refresh jwt or is corrupted");
        }

        if(!persistenceAdapter.existsRefreshTokenById(jwtService.getTokenIdFromRefreshJwt(jwt))){
            throw new JWTVerificationException("This refresh token is no longer valid");
        }
    }

    @Override
    protected Object loadPrincipal(String jwt) throws JWTVerificationException {
        UUID tokenId = jwtService.getTokenIdFromRefreshJwt(jwt);
        UUID userId = jwtService.getUserIdFromRefreshJwt(jwt);
        //In this project: Principal for RefreshJWT ->
        // Token (refresh token id + Player (only stores player id, rest null))
        return new Token(tokenId,Player.builder().playerId(userId).build());
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
