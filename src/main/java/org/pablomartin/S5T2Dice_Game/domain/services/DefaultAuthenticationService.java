package org.pablomartin.S5T2Dice_Game.domain.services;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.Utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;
import org.pablomartin.S5T2Dice_Game.exceptions.PlayerNotFoundException;
import org.pablomartin.S5T2Dice_Game.exceptions.UsernameNotAvailableException;
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
    public void invalidateRefreshToken(UUID refreshTokenId) {
        persistenceAdapter.deleteRefreshTokenById(refreshTokenId);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void invalidateAllRefreshToken(UUID ownerId) {
        persistenceAdapter.deleteAllRefreshTokenFromPlayer(ownerId);
    }

    /*
    Player should be full populated and equivalent to persisted,
    to avoid potentials bugs.
     */
    private Token saveNewRefreshToken(PlayerOld owner){
        return persistenceAdapter.saveOrUpdate(new Token(owner));
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token allowNewRefreshTokenFromLogin(String username) {
        PlayerOld playerOld = persistenceAdapter.findPlayerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: "+username));
        //exception shouldn't be thown, due is the same method to authenticate Basic Authentication
        return saveNewRefreshToken(playerOld);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token resetRefreshTokens(UUID ownerId) {;
        PlayerOld playerOld = persistenceAdapter.findPlayerById(ownerId)
                .orElseThrow(() -> new PlayerNotFoundException(ownerId));
        invalidateAllRefreshToken(ownerId);
        return saveNewRefreshToken(playerOld);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token performSingup(PlayerOld playerOld) { //player .asAnnonimous or .asEwgistered
        assertUsernameAvailable(playerOld);
        playerOld.setRegisterDate(TimeUtils.nowSecsTruncated());
        playerOld = persistenceAdapter.saveOrUpdate(playerOld);
        return saveNewRefreshToken(playerOld);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public PlayerOld uptadeBasicCredentials(PlayerOld credentialsProvider) {
        assertUsernameAvailable(credentialsProvider);
        PlayerOld target = persistenceAdapter
                .findPlayerById(credentialsProvider.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(credentialsProvider.getPlayerId()));
        target.updateCredentials(credentialsProvider);
        return persistenceAdapter.saveOrUpdate(target);
        //refresh tokens are still valid
    }

    private void assertUsernameAvailable(PlayerOld playerOld){
        String username = playerOld.getUsername();
        if(playerOld.hasUsernameToCheck() && persistenceAdapter.isUsernameRegistered(username)){
            throw new UsernameNotAvailableException(username);
        }
    }

}
