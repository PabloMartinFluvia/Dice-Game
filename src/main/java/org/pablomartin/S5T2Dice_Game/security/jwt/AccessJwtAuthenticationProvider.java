package org.pablomartin.S5T2Dice_Game.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.PersistenceAdapter;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.domain.services.JwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("AccessProvider")
public class AccessJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider{

    private final PersistenceAdapter persistenceAdapter;

    public AccessJwtAuthenticationProvider(JwtService jwtService, PersistenceAdapter persistenceAdapter) {
        super(jwtService);
        this.persistenceAdapter = persistenceAdapter;
    }

    @Transactional(transactionManager = "chainedTransactionManager")
    @Override
    //valid jwt
    protected void preValidate(String jwt) throws JWTVerificationException {
        if(!jwtService.isValidAccessJwt(jwt)){
            throw new JWTVerificationException("This bearer token it's not an access jwt or is corrupted");
        }
        //the owner that claims the jwt exists
        UUID ownerId = jwtService.getUserIdFromAccesJwt(jwt);
        if(!persistenceAdapter.existsPlayerById(ownerId)){
            //in case user has been deleted, AFTER providing the access jwt
            throw new JWTVerificationException("This bearer acces token does not belong anymore to any user.");
        }

        //the rest of the claims in the jwt are still valid

        //role
        Role claimed = jwtService.getUserRoleFormAccessJwt(jwt);
        Role actual = persistenceAdapter.findPlayerRole(ownerId).orElse(null);
        if(!actual.equals(claimed)){
            //in case, due any reason, user's role changed AFTER providing the access jwt
            throw new JWTVerificationException("This bearer access token's claim 'role' it's no longer valid");
        }
    }

    @Override
    protected Object loadPrincipal(String jwt) throws JWTVerificationException {
        //In this project: Principal for AccesJWT -> UUID (player id)
        return jwtService.getUserIdFromAccesJwt(jwt);
    }

    @Override
    protected Collection<? extends GrantedAuthority> loadAuthorities(String jwt) throws JWTVerificationException {
        return jwtService.getUserAuthoritiesFromAccesJwt(jwt)
                .stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }
}
