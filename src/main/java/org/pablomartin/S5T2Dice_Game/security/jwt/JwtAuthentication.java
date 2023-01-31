package org.pablomartin.S5T2Dice_Game.security.jwt;

import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;
import java.util.Collections;

public class JwtAuthentication extends AbstractAuthenticationToken {

    /*
    In this project principal object type is:
    For AccesJWT -> UUID (player id)
    For RefreshJWT -> Token (refresh token id + Player (only stores player id, rest null))
     */
    private Object principal;
    private String jwt;

    public JwtAuthentication(String jwt) {
        //unverified authentication
        super(Collections.emptyList());
        Assert.hasText(jwt.toString(), "token cannot be empty");
        this.principal = null;
        this.jwt = jwt;
        super.setAuthenticated(false);
    }

    public JwtAuthentication (Object principal, String jwt, Collection<? extends GrantedAuthority> authorities){
        //verified authentication
        super(authorities);
        Assert.hasText(jwt.toString(), "token cannot be empty");
        this.principal = principal;
        this.jwt = jwt;
        super.setAuthenticated(true);
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public String getName() {
        //ckeck if inherited method fits with this type of principal
        if (this.getPrincipal() instanceof Token) {
            return ((Token) this.getPrincipal()).getOwner().getPlayerId().toString();
        }
        return this.getPrincipal().toString();
    }
}
