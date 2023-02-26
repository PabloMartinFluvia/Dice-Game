package org.pablomartin.S5T2Dice_Game.security.jwt.providers;

import com.auth0.jwt.exceptions.JWTVerificationException;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.data.SecurityPersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.RefreshTokenPrincipal;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Component("RefreshProvider")
@Log4j2
public class RefreshJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider {

    public RefreshJwtAuthenticationProvider(JwtService jwtService, SecurityPersistenceAdapter adapter) {
        super(jwtService, adapter);
        this.tokenType = "refresh";
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    protected void preValidate(String jwt) throws JWTVerificationException {
        //valid jwt
        if(!jwtService.isValidRefreshJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not a "+tokenType+" jwt or is corrupted");
        }
        UUID tokenId = jwtService.getTokenIdFromRefreshJwt(jwt);

        this.claimsStored = adapter.loadCredentialsByRefreshTokenId(tokenId)
                //if token not found (due logout, reset, etc..)
                .orElseThrow(() -> new JWTVerificationException
                        ("This bearer "+tokenType+" JWT has been disabled/removed"));

        if(claimsStored.getUserId() == null){
            //when the refresh token is stored, but not linked to any user/player -> bug
            log.error("Error: found a "+tokenType+" token not related to any user -> bug");
            adapter.removeRefreshToken(tokenId);
            throw  new JwtAuthenticationException("This "+tokenType+" JWT does not belong anymore to any user. " +
                    "Request for a new "+tokenType+" JWT");
        }

        /*
        refresh jwt claims:
            owner id in 'subject'
         */

        UUID userIdClaimed = jwtService.getUserIdFromRefreshJwt(jwt);
        if(!userIdClaimed.equals(claimsStored.getUserId())){
            log.error("The owner/player id has been modified after providing the jwt or " +
                    "the "+tokenType+" has been signed with an invalid 'subject' claim.");
            adapter.removeRefreshToken(tokenId);
            throw new JWTVerificationException("The "+tokenType+" JWT's claim 'subject' it's not the" +
                    " owner of this token. Request for a new "+tokenType+" JWT");
        }
    }

    @Override
    protected RefreshTokenPrincipal loadPrincipal(String jwt){
        UUID tokenId = jwtService.getTokenIdFromRefreshJwt(jwt);
        return claimsStored != null ? claimsStored.toRefreshTokenPrincipal(tokenId):null;
    }

}
