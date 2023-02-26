package org.pablomartin.S5T2Dice_Game.domain.data.repos.projections;

import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.DefaultPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PrincipalProvider;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.util.Assert;

import java.util.Set;
import java.util.UUID;

public interface PrincipalProjection {

    UUID getPlayerId();

    String getUsername();

    SecurityProjection getSecurityDetails();

    interface SecurityProjection{

        String getPassword();

        Role getRole();

        //no others granted authorities
    }

    default PrincipalProvider toPrincipalProvider(){
        Assert.notNull(getSecurityDetails(), "player entity/doc must have security details not null."); //at least has role stored
        return DefaultPrincipal.builder()
                .userId(getPlayerId())
                .username(getUsername())
                .password(getSecurityDetails().getPassword())
                .authorities(Set.of(
                        new SimpleGrantedAuthority(getSecurityDetails().getRole().withPrefix())))
                //no others granted authorities
                .build();
    }
}
