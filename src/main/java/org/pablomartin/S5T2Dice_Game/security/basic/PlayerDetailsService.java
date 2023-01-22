package org.pablomartin.S5T2Dice_Game.security.basic;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
