package org.pablomartin.S5T2Dice_Game.security.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import org.pablomartin.S5T2Dice_Game.domain.data.repos.old.PersistenceAdapterV2;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.pablomartin.S5T2Dice_Game.domain.services.old.JwtService;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.UUID;
import java.util.stream.Collectors;

@Component("AccessProvider")
public class AccessJwtAuthenticationProvider extends AbstractJwtAuthenticationProvider{

    private final PersistenceAdapterV2 persistenceAdapterV2;

    public AccessJwtAuthenticationProvider(JwtService jwtService, PersistenceAdapterV2 persistenceAdapterV2) {
        super(jwtService);
        this.persistenceAdapterV2 = persistenceAdapterV2;
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
        if(!persistenceAdapterV2.existsPlayer(ownerId)){
            //in case user has been deleted, AFTER providing the access jwt
            throw new JWTVerificationException("This bearer acces token does not belong anymore to any user.");
        }

        //the rest of the claims in the jwt are still valid

        //TODO: check (depending WITCH CLAIM CONTAINS) if username or role still matches

        //role
        Role claimed = jwtService.getUserRoleFormAccessJwt(jwt);
        Role actual = persistenceAdapterV2.findPlayerById(ownerId)
                            .map(player -> player.getRole())
                            .orElse(null);
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
