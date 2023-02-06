package org.pablomartin.S5T2Dice_Game.security.principalsModels;

import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/**
 * To load player claims from DB to store in principals
 */
public interface PlayerCredentials {
    
    //to set:
    // id / username / password / grantedAuthorities (at least one role with prefix)

    UUID getUserId();

    String getUsername();

    Collection<? extends GrantedAuthority> getAuthorities();

    Role getUserRole();
    
    BasicPrincipal toBasicPrincipal();

    //In this project: Principal for AccesJWT -> UUID (player id)
    TokenPrincipal toAccessTokenPrincipal();


    RefreshTokenPrincipal toRefreshTokenPrincipal(UUID refreshTokenId);
}
