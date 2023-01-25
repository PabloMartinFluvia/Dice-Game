package org.pablomartin.S5T2Dice_Game.domain.services;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.Utils.TimeUtils;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
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
    private Token saveNewRefreshToken(Player owner){
        return persistenceAdapter.saveOrUpdate(new Token(owner));
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token allowNewRefreshTokenFromLogin(String username) {
        Player player = persistenceAdapter.findPlayerByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found: "+username));
        //exception shouldn't be thown, due is the same method to authenticate Basic Authentication
        return saveNewRefreshToken(player);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token resetRefreshTokens(UUID ownerId) {;
        Player player = persistenceAdapter.findPlayerById(ownerId)
                .orElseThrow(() -> new PlayerNotFoundException(ownerId));
        invalidateAllRefreshToken(ownerId);
        return saveNewRefreshToken(player);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Token performSingup(Player player) { //player .asAnnonimous or .asEwgistered
        assertUsernameAvailable(player);
        player.setRegisterDate(TimeUtils.nowSecsTruncated());
        player = persistenceAdapter.saveOrUpdate(player);
        return saveNewRefreshToken(player);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public Player uptadeBasicCredentials(Player credentialsProvider) {
        assertUsernameAvailable(credentialsProvider);
        Player target = persistenceAdapter
                .findPlayerById(credentialsProvider.getPlayerId())
                .orElseThrow(() -> new PlayerNotFoundException(credentialsProvider.getPlayerId()));
        target.updateCredentials(credentialsProvider);
        return persistenceAdapter.saveOrUpdate(target);
        //refresh tokens are still valid
    }

    private void assertUsernameAvailable(Player player){
        String username = player.getUsername();
        if(player.hasUsernameToCheck() && persistenceAdapter.isUsernameRegistered(username)){
            throw new UsernameNotAvailableException(username);
        }
    }

}
