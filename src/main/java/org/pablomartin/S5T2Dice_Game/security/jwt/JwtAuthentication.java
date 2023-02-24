package org.pablomartin.S5T2Dice_Game.security.jwt;

import org.pablomartin.S5T2Dice_Game.security.old.RefreshTokenPrincipal;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.util.Assert;

import java.util.Collection;

public class JwtAuthentication extends AbstractAuthenticationToken {

    private Object principal;
    private String jwt;

    public JwtAuthentication(String jwt) {
        //unverified authentication
        super(null); //super -> empty list
        Assert.hasText(jwt, "token cannot be empty");
        this.principal = null;
        this.jwt = jwt;
        super.setAuthenticated(false);
    }

    public JwtAuthentication (Object principal, String jwt, Collection<? extends GrantedAuthority> authorities){
        //verified authentication
        super(authorities);
        Assert.notNull(principal,"authentication principal can't be null");
        Assert.hasText(jwt, "token cannot be empty");
        this.principal = principal;
        this.jwt = jwt;
        super.setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return principal;
    }

    @Override
    public Object getCredentials() {
        return jwt;
    }

    @Override
    public String getName() {
        if (principal instanceof RefreshTokenPrincipal) {
            return ((RefreshTokenPrincipal) principal).getUsername();
        }
        return super.getName(); //at the end: principal to String -> (in access token user id)
    }
}
