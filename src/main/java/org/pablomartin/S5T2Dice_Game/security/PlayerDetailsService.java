package org.pablomartin.S5T2Dice_Game.security;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PlayerDetailsService implements UserDetailsService {

    private final PersistenceAdapter persistenceAdapter;

    @Override
    public PlayerDetails loadUserByUsername(String username) throws UsernameNotFoundException{
        Optional<Player> player = persistenceAdapter.findPlayerByUsername(username);
        if(player.isPresent()){
            return new PlayerDetails(player.get());
        }else{
            throw new UsernameNotFoundException("Username not found: "+username);
        }

        /*
        Player player = persistenceAdapter.findPlayerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: "+username));
         */

    }


}
