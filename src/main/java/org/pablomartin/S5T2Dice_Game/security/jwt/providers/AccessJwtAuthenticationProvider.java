package org.pablomartin.S5T2Dice_Game.security.jwt.providers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.TokenPrincipal;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("AccessProvider")
public class AccessJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider {

    public AccessJwtAuthenticationProvider(JwtService jwtService, SecurityPersistenceAdapter adapter) {
        super(jwtService, adapter);
    }

    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        if(!jwtService.isValidAccessJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not an access jwt or is corrupted");
        }
        //the owner claim
        UUID ownerIdClaimed = jwtService.getUserIdFromAccesJwt(jwt);
        String usernameClaimed = jwtService.getUsernameFromAccessJwt(jwt);
        Role roleClaimed = jwtService.getRoleFromAccessJwt(jwt);

        credentials = adapter.findOwnerById(ownerIdClaimed)
                //in case user has been deleted, AFTER providing the access jwt
                .orElseThrow(() -> new JwtAuthenticationException("The owner of this access token doesn't exists"));

        if(usernameClaimed != null){
            if(!usernameClaimed.equals(credentials.getUsername())){
                throw new JWTVerificationException("This access JWT is no longer valid (username has been updated). " +
                        "Request a new access JWT.");
            }
        }

        if(roleClaimed != null){
            if(!roleClaimed.equals(credentials.getUserRole())){
                throw new JWTVerificationException("This access JWT is no longer valid (role has been updated). " +
                        "Request a new access JWT.");
            }
        }
    }

    @Override
    protected TokenPrincipal loadPrincipal(String jwt){
        return credentials.toAccessTokenPrincipal();
    }

}
