package org.pablomartin.S5T2Dice_Game.security.principalsModels;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.security.basic.BasicPrincipal;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.UUID;

/*
Populated when security filters try to authenticate a request calling the DB
    (to compare what's stored vs requested).
Used for provide the missing data (not provided on request) to
 full populate all possibles Authentication's Principal types.
 */
public interface PrincipalProvider {

    UUID getUserId();

    String getUsername();

    String getPassword();

    Collection<? extends GrantedAuthority> getAuthorities();

    Role getUserRole();
    
    BasicPrincipal toBasicPrincipal();

    TokenPrincipal toAccessTokenPrincipal();

    RefreshTokenPrincipal toRefreshTokenPrincipal(UUID refreshTokenId);
}
