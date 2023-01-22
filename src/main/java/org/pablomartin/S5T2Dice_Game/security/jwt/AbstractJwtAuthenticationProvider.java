package org.pablomartin.S5T2Dice_Game.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

@RequiredArgsConstructor
@Log4j2
public abstract class AbstractJwtAuthenticationProvider implements AuthenticationProvider {

    protected final JwtService jwtService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Assert.isInstanceOf(JwtAuthentication.class, authentication, () ->
                "JwtAuthenticationProporcionador only can authenticate instances of JwtAuthentication");
        String jwt = (String) authentication.getCredentials();
        if (jwt == null){
            this.log.debug("Jwt Authentication provider called with null credentials.");
            throw new BadCredentialsException("Valid Bearer token not provided");
        }
        JwtAuthentication validatedAuthentication = createSuccessfulAuthentication(jwt);
        validatedAuthentication.setDetails(authentication.getDetails());
        return validatedAuthentication;
    }


    @Override
    public boolean supports(Class<?> authentication) {
        return JwtAuthentication.class.isAssignableFrom(authentication);
    }

    private JwtAuthentication createSuccessfulAuthentication (String jwt) throws AuthenticationException{
        try {
            preValidate(jwt);
            Object principal = loadPrincipal(jwt);
            Collection<? extends GrantedAuthority> authorities = loadAuthorities(jwt);
            return new JwtAuthentication(principal, jwt, authorities);

        }catch (JWTVerificationException failed){
            //Exceptions when decoding doesn't extend from AuthenticationExteption.
            //-> Catch and Throw
            throw new JwtAuthenticationException(failed.getMessage());
        }
    }

    protected abstract void preValidate(String jwt)throws JWTVerificationException  ;

    protected abstract Object loadPrincipal(String jwt) throws JWTVerificationException;

    protected abstract Collection<? extends GrantedAuthority> loadAuthorities(String jwt) throws JWTVerificationException;
}
