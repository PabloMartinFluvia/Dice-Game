package org.pablomartin.S5T2Dice_Game.security.basic;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.old.PersistenceAdapterV2;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PlayerDetailsService implements UserDetailsService {

    private final PersistenceAdapterV2 persistenceAdapterV2;

    @Override
    public DefaultBasicPrincipal loadUserByUsername(String username) throws UsernameNotFoundException{
        //TODO: assert principal contains authorities, at least one simple granted authority "ROLE_XXX"
        Optional<PlayerOld> player = persistenceAdapterV2.findPlayerByUsername(username);
        if(player.isPresent()){
            return new DefaultBasicPrincipal(player.get());
        }else{
            throw new UsernameNotFoundException("Username not found: "+username);
        }

        /*
        Player player = persistenceAdapter.findPlayerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: "+username));
         */

    }
}
