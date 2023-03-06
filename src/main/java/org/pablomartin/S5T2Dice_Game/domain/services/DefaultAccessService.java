package org.pablomartin.S5T2Dice_Game.domain.services;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.data.SettingsPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.AccessInfo;
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
    private final SettingsPersistenceAdapter adapter;

    protected boolean existsPlayer(@NotNull UUID playerId) {
        return adapter.existsPlayer(playerId);
    }

    private void assertUsernameNoConflict(@NotNull NewPlayerInfo details){
        if(details.isUsernameProvided()){
           assertUsernameAvailable(details.getUsername());
        }
    }

    private void assertUsernameAvailable(@NotNull String username){
        if(!adapter.isUsernameAvailable(username)){
            throw new UsernameNotAvailableException(username);
        }
    }

    private AccessInfo populateFullAccessInfo(@NotNull SecurityClaims claims){
        String accessJwt = jwtService.createAccessJwt(claims);
        String refreshJwt = jwtService.createRefreshJwt(claims);
        return claims.toAppAccess(accessJwt, refreshJwt);
    }

    public AccessInfo createAccessJWT(SecurityClaims claims) {
        String accessJwt = jwtService.createAccessJwt(claims);
        return claims.toAppAccess(accessJwt, null);
    }


    @Transactional(transactionManager = "chainedTransactionManager")
    public AccessInfo performSingUp(NewPlayerInfo details) {
        assertUsernameNoConflict(details);
        SecurityClaims credentials = adapter.newPlayerWithRefreshToken(details);
        return populateFullAccessInfo(credentials);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    public AccessInfo updateCredentials(NewPlayerInfo details) {
        assertPlayerExists(details.getPlayerAuthenticatedId().orElse(null));
        assertUsernameNoConflict(details);
        SecurityClaims credentials = adapter.updateCredentials(details);

        if(details.isUsernameProvided()){
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
    public AccessInfo createJWTS(@NotNull SecurityClaims claims) {
        return generateRefreshToken(claims);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    public AccessInfo resetTokensFromOwner(@NotNull SecurityClaims claims) {
        adapter.deleteAllRefreshTokensByUser(claims.getPlayerId());
        return generateRefreshToken(claims);
    }

    //Callers must be transactional!
    private AccessInfo generateRefreshToken(@NotNull SecurityClaims claims) {
        claims = adapter.allowNewRefreshToken(claims); //throws exception if player not found
        return populateFullAccessInfo(claims);
    }


    @Transactional(transactionManager = "chainedTransactionManager")
    public void invalidateAllRefreshTokensFromOwner(@NotNull UUID playerId) {
        adapter.deleteAllRefreshTokensByUser(playerId);
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    public void invalidateRefreshToken(UUID refreshTokenId) {
        adapter.removeRefreshToken(refreshTokenId);
    }


    @Transactional(transactionManager = "chainedTransactionManager")
    public void deleteUser(@NotNull UUID notAdminId) {
        Optional<Role> role = adapter.findUserRole(notAdminId);
        if(role.isPresent()){
            if(!role.get().equals(Role.ADMIN)){
                adapter.deleteUser(notAdminId);
            }else {
                throw new AdminOperationsException("An ADMIN can't be deleted");
            }
        }
    }
}
