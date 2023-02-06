package org.pablomartin.S5T2Dice_Game.security.basic;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.PlayerCredentials;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PlayerDetailsService implements UserDetailsService {

    private final SecurityPersistenceAdapter adapter;

    //DaoAuthenticationProvider never gives a null username
    @Override
    public BasicPrincipal loadUserByUsername(@NotNull String username) throws UsernameNotFoundException{
        if (username.equalsIgnoreCase(DiceGameContext.getDefaultUsername())){
            throw new UsernameNotFoundException(
                    "Anonymous users are not allowed to be authenticated by Basic Filter.");
            /*
            Note: any username provided by any user must be different to the default to be valid.
            So never a registered user will have a username equals to the default (reserved for anonymous).
             */
        }
        PlayerCredentials credentials = adapter.findOwnerById(username)
                .orElseThrow(()-> new UsernameNotFoundException("Username not found: "+username));
        return credentials.toBasicPrincipal();
    }
}
