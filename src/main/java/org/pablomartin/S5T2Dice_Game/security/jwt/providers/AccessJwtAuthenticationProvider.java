package org.pablomartin.S5T2Dice_Game.security.jwt.providers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.UUID;

@Component("AccessProvider")
public class AccessJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider {

    //@Autowired
    public AccessJwtAuthenticationProvider(JwtService jwtService, SecurityPersistenceAdapter adapter) {
        super(jwtService, adapter);
        this.tokenType = "access";
    }

    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        if(!jwtService.isValidAccessJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not an access jwt or is corrupted");
        }

        UUID userIdClaimed = jwtService.getUserIdFromAccessJwt(jwt);
        String usernameClaimed = jwtService.getUsernameFromAccessJwt(jwt);
        Role roleClaimed = jwtService.getRoleFromAccessJwt(jwt);

        this.principalData = adapter.loadCredentialsByUserId(userIdClaimed)
                //in case user has been deleted, AFTER providing the access jwt
                .orElseThrow(() -> new JwtAuthenticationException("The owner of this access token doesn't exists"));

        if(usernameClaimed != null){
            checkClaimsSynchronized(usernameClaimed, principalData.getUsername(),"username");
        }

        if(roleClaimed != null){
            checkClaimsSynchronized(roleClaimed, principalData.getUserRole(),"role");
        }
    }

    private void checkClaimsSynchronized(Object inJwt, Object inDb, String claimType){
        if(!Objects.equals(inJwt,inDb)){
            throw new JWTVerificationException("This "+tokenType+" JWT is no longer valid: "
                    +claimType+" has been updated. -> Request a new "+tokenType+" JWT.");

        }
    }

    @Override
    protected TokenPrincipal loadPrincipal(String jwt){
        return principalData != null ? principalData.toAccessTokenPrincipal(): null;
    }

}
