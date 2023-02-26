package org.pablomartin.S5T2Dice_Game.domain.data.repos.projections;

import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.DefaultPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.Set;


public interface PrincipalProjectionFromRefreshToken {

    PrincipalProjection getPlayer();

    default PrincipalProvider toPrincipalProvider(){
        if(getPlayer() != null){
            return DefaultPrincipal.builder()
                    .userId(getPlayer().getPlayerId())
                    .username(getPlayer().getUsername())
                    .password(getPlayer().getSecurityDetails().getPassword())
                    .authorities(Set.of(
                            new SimpleGrantedAuthority(getPlayer().getSecurityDetails()
                                    .getRole().withPrefix())))
                    //no others granted authorities
                    .build();
        }else {
            return DefaultPrincipal.builder().build();
        }
    }
}
