package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.AccessPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.InfoForAppAccess;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.exceptions.AdminOperationsException;
import org.pablomartin.S5T2Dice_Game.exceptions.UsernameNotAvailableException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DefaultAccessService extends AbstractService implements AccessService{

    private final  JwtService jwtService;
    private final AccessPersistenceAdapter adapter;

    @Override
    protected boolean existsPlayer(@NotNull UUID playerId) {
        return adapter.existsPlayer(playerId);
    }

    private void assertUsernameNoConflict(@NotNull NewPlayerInfo playerInfo){
        if(playerInfo.isUsernameProvided()){
           assertUsernameAvailable(playerInfo.getUsername());
        }
    }

    private void assertUsernameAvailable(@NotNull String username){
        if(!adapter.isUsernameAvailable(username)){
            throw new UsernameNotAvailableException(username);
        }
    }

    private InfoForAppAccess provideFullAccessDetails(@NotNull SecurityClaims credentials){
        String accessJwt = jwtService.createAccessJwt(credentials);
        String refreshJwt = jwtService.createRefreshJwt(credentials);
        return credentials.toAppAccess(accessJwt, refreshJwt);
    }

    @Override
    public InfoForAppAccess createAccessJWT(SecurityClaims credentials) {
        String accessJwt = jwtService.createAccessJwt(credentials);
        return credentials.toAppAccess(accessJwt, null);
    }


    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public InfoForAppAccess performSingUp(NewPlayerInfo newPlayerInfo) {
        assertUsernameNoConflict(newPlayerInfo);
        SecurityClaims credentials = adapter.newPlayerWithRefreshToken(newPlayerInfo);
        return provideFullAccessDetails(credentials);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public InfoForAppAccess updateCredentials(NewPlayerInfo playerInfo) {
        assertPlayerExists(playerInfo.getPlayerAuthenticatedId().orElse(null));
        assertUsernameNoConflict(playerInfo);
        SecurityClaims credentials = adapter.updateCredentials(playerInfo);

        if(playerInfo.isUsernameProvided()){
            /*
             CAN be false only when player was registered previously and only wants to update password.
             When true (player was anonymous or registered player wants to update username):
                -> old claims in access jwt won't match anymore
             */
            return createAccessJWT(credentials);
        }
        return credentials.toAppAccess(null, null); //old jwt still valid
    }



    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public InfoForAppAccess createJWTS(@NotNull SecurityClaims credentials) {
        return generateRefreshToken(credentials);
    }

    //Callers must be transactional!
    private InfoForAppAccess generateRefreshToken(@NotNull SecurityClaims credentials) {
        credentials = adapter.allowNewRefreshToken(credentials); //throws exception if player not found
        return provideFullAccessDetails(credentials);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public InfoForAppAccess resetTokensFromOwner(@NotNull SecurityClaims credentials) {
        deleteAllRefreshTokensByUser(credentials.getPlayerId());
        return generateRefreshToken(credentials);
    }

    //Callers must be transactional!
    private void deleteAllRefreshTokensByUser(@NotNull UUID ownerId){
        adapter.deleteAllRefreshTokensByUser(ownerId);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void invalidateAllRefreshTokensFromOwner(UUID ownerId) {
        deleteAllRefreshTokensByUser(ownerId);
    }


    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void deleteUser(@NotNull UUID userId) {
        Optional<Role> role = adapter.findUserRole(userId);
        if(role.isPresent()){
            if(!role.get().equals(Role.ADMIN)){
                adapter.deleteUser(userId);
            }else {
                throw new AdminOperationsException("An ADMIN can't be deleted");
            }
        }
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    public void invalidateRefreshToken(UUID refreshTokenId) {
        adapter.removeRefreshToken(refreshTokenId);
    }


}
