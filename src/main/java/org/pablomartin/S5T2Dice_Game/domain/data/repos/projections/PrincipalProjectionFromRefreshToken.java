package org.pablomartin.S5T2Dice_Game.domain.data.repos.projections;

import org.pablomartin.S5T2Dice_Game.security.principalsModels.DefaultPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;

public interface PrincipalProjectionFromRefreshToken {

    PrincipalProjection getPlayer();

    default PrincipalProvider toPrincipalProvider(){
        return DefaultPrincipal.builder()
                .userId(getPlayer().getPlayerId())
                .username(getPlayer().getUsername())
                .password(getPlayer().getSecurityDetails().getPassword())
                .authorities(Set.of(
                        new SimpleGrantedAuthority(getPlayer().getSecurityDetails()
                                .getRole().withPrefix())))
                //no others granted authorities
                .build();
    }
}
