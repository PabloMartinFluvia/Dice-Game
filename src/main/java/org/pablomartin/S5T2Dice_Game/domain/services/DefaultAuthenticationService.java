package org.pablomartin.S5T2Dice_Game.domain.services;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AccessInfoDto;
import org.pablomartin.S5T2Dice_Game.domain.models.RefreshToken;
import org.pablomartin.S5T2Dice_Game.exceptions.UsernameNotAvailableException;
import org.pablomartin.S5T2Dice_Game.security.PlayerDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService{

    private final PersistenceAdapter persistenceAdapter;

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public RefreshToken performNewSingup(Player player) {
        assertUsernameAvailable(player);
        player = persistenceAdapter.saveNewPlayer(player);
        RefreshToken refreshToken = persistenceAdapter.saveNewRefreshToken(new RefreshToken(player));
        return refreshToken;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public  RefreshToken performLogin(String username) {
        Player player = persistenceAdapter.findPlayerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: "+username));
        //exception shouldn't be thown, due is the same method to authenticate Basic Authentication
        RefreshToken refreshToken = persistenceAdapter.saveNewRefreshToken(new RefreshToken(player));
        return refreshToken;
    }

    private void assertUsernameAvailable(Player player){
        String username = player.getUsername();
        if(!player.isAnnonimus() && persistenceAdapter.isUsernameRegistered(username)){
            // name already exists AND it's not the default
            throw new UsernameNotAvailableException(username);
        }
    }
}
