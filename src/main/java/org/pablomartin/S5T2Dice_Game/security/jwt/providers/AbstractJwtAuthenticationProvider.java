package org.pablomartin.S5T2Dice_Game.security.jwt.providers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.pablomartin.S5T2Dice_Game.security.jwt.JwtAuthentication;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

@RequiredArgsConstructor
@Log4j2
public abstract class AbstractJwtAuthenticationProvider implements AuthenticationProvider {

    protected final JwtService jwtService;

    protected final SecurityPersistenceAdapter adapter;

    protected PrincipalProvider principalData;

    protected String tokenType;

    @Override
    public Authentication authenticate(Authentication unverified) throws AuthenticationException {
        Assert.isInstanceOf(JwtAuthentication.class, unverified, () ->
                "JwtAuthenticationProvider only can authenticate instances of JwtAuthentication");
        String jwt = (String) unverified.getCredentials();
        if (jwt == null){
            log.debug("Jwt Authentication Provider called to authenticate with null credentials.");
            throw new BadCredentialsException("Valid Bearer token not provided");
        }
        JwtAuthentication authenticated = createSuccessfulAuthentication(jwt);
        authenticated.setDetails(unverified.getDetails());
        return authenticated;
    }


    @Override
    public boolean supports(Class<?> authenticationType) {
        return JwtAuthentication.class.isAssignableFrom(authenticationType);
    }

    private JwtAuthentication createSuccessfulAuthentication (String jwt) throws AuthenticationException{
        try {
            preValidate(jwt);
            Object principal = loadPrincipal(jwt);
            Collection<? extends GrantedAuthority> authorities = loadAuthorities();
            return JwtAuthentication.asAuthenticated(principal,jwt,authorities);
        }catch (JWTVerificationException failed){
            //Exceptions when decoding (with external library) doesn't extend from AuthenticationException.
            //-> Catch and Throw
            throw new JwtAuthenticationException(failed.getMessage());
        }finally {
            principalData = null;
        }
    }

    protected abstract void preValidate(String jwt)throws JWTVerificationException  ;

    protected abstract Object loadPrincipal(String jwt) throws JWTVerificationException;

    protected Collection<? extends GrantedAuthority> loadAuthorities(){
        return principalData != null ? principalData.getAuthorities() : Collections.EMPTY_SET;
    }
}
