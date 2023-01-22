package org.pablomartin.S5T2Dice_Game.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.stream.Collectors;

@Component("AccessProvider")
public class AccessJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider{

    public AccessJwtAuthenticationProvider(JwtService jwtService) {
        super(jwtService);
    }

    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        if(!jwtService.isValidAccessJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not an access jwt or is corrupted");
        }
    }

    @Override
    protected Object loadPrincipal(String jwt) throws JWTVerificationException {
        //In this project: Principal for AccesJWT -> UUID (player id)
        return jwtService.getUserIdFromAccesJwt(jwt);
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadAuthorities(String jwt) throws JWTVerificationException {
        return jwtService.getUserAuthoritiesFromAccesJwt(jwt)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
