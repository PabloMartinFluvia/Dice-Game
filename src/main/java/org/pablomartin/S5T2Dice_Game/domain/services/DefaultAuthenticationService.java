package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.AssertTrue;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.exceptions.UsernameNotAvailableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultAuthenticationService implements AuthenticationService{

    private final PersistenceAdapter persistenceAdapter;

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token performNewSingup(Player player) {
        assertUsernameAvailable(player);
        player = persistenceAdapter.saveNewPlayer(player);
        Token refreshToken = persistenceAdapter.saveNewRefreshToken(new Token(player));
        return refreshToken;
    }

    private void assertUsernameAvailable(Player player){
        String username = player.getUsername();
        if(!player.isAnnonimus() && persistenceAdapter.isUsernameRegistered(username)){
            // name already exists AND it's not the default
            throw new UsernameNotAvailableException(username);
        }
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token performLogin(String username) {
        Player player = persistenceAdapter.findPlayerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: "+username));
        //exception shouldn't be thown, due is the same method to authenticate Basic Authentication
        Token refreshToken = persistenceAdapter.saveNewRefreshToken(new Token(player));
        return refreshToken;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void invalidateRefreshToken(UUID refreshTokenId) {
        //Assert.isTrue(persistenceAdapter.existsRefreshTokenById(refreshTokenId),"This refresh token must exist.");
        persistenceAdapter.deleteRefreshTokenById(refreshTokenId);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void invalidateAllRefreshToken(UUID playerId) {
        persistenceAdapter.deleteAllRefreshTokenFromPlayer(playerId);
    }


}
