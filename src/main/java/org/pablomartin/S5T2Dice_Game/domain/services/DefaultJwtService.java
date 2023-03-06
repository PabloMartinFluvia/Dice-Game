package org.pablomartin.S5T2Dice_Game.domain.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import jakarta.validation.constraints.NotNull;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@PropertySource("classpath:values.properties")
@Log4j2
public class DefaultJwtService implements JwtService{

    private static final String ROLE_CLAIM = "role";

    private static final String NAME_CLAIM = "username";

    private static final String TOKEN_ID_CLAIM = "tokenId";

    private final String issuer;

    private final long accessTokenExpirationMs; //en milisegons

    private final long refreshTokenExpirationMs; //en milisegons
    private final Algorithm accessTokenAlgorithm;

    private final Algorithm refreshTokenAlgorithm;

    private final JWTVerifier accessTokenVerifier;

    private final JWTVerifier refreshTokenVerifier;

    public DefaultJwtService(@Value("${jwt.issuer}") String issuer,
                                @Value("${jwt.access.expirationMinutes}") long accessTokenExpirationMinutes,
                                @Value("${jwt.refresh.expirationDays}") long refreshTokenExpirationDays,
                                @Value("${jwt.access.secret}") String accessTokenSecret,
                                @Value("${jwt.refresh.secret}") String refreshTokenSecret) {

        this.issuer = issuer;
        accessTokenExpirationMs = accessTokenExpirationMinutes*60*1000;
        refreshTokenExpirationMs = refreshTokenExpirationDays*24*60*60*1000;
        accessTokenAlgorithm =Algorithm.HMAC256(accessTokenSecret);
        refreshTokenAlgorithm =Algorithm.HMAC256(refreshTokenSecret);
        accessTokenVerifier = initAccessVerifier();
        refreshTokenVerifier = initRefreshVerifier();
    }

    private JWTVerifier initAccessVerifier(){
        return JWT
                .require(accessTokenAlgorithm)
                .withIssuer(issuer)
                //podria indicar més restriccions per a ser vàlid:
                //ex: que tingui un o N específic claim tingui un determinat valor
                .build();
    }
    private JWTVerifier initRefreshVerifier(){
        return JWT
                .require(refreshTokenAlgorithm)
                //idem al mètode anterior
                .withIssuer(issuer)
                .build();
    }

    @Override
    public String createAccessJwt(@NotNull SecurityClaims claims) {
        Assert.notNull(claims, "credentials must be not null");
        long now = System.currentTimeMillis();
        String claim;
        String value;
        if(Objects.equals(claims.getRole(),Role.VISITOR)){
            claim = ROLE_CLAIM;
            value = claims.getRole().toString();
        }else {
            claim = NAME_CLAIM;
            value = claims.getUsername();
        }
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(claims.getPlayerId())) //idem a ID_CLAIM;
                .withClaim(claim, value)
                .withIssuedAt(new Date(now))
                .withNotBefore(new Date(now))
                .withExpiresAt(new Date(now + accessTokenExpirationMs))
                .sign(accessTokenAlgorithm);
    }

    @Override
    public String createRefreshJwt(SecurityClaims claims) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(claims.getPlayerId()))
                .withClaim(TOKEN_ID_CLAIM, String.valueOf(claims.getRefreshTokenId()))
                .withIssuedAt(new Date(now))
                .withNotBefore(new Date(now))
                .withExpiresAt(new Date(now + refreshTokenExpirationMs))
                .sign(refreshTokenAlgorithm);
    }

    @Override
    public boolean isValidAccessJwt(String jwt) {
        return decodeAccessToken(jwt).isPresent();
    }

    @Override
    public boolean isValidRefreshJwt(String jwt) {
        return decodeRefreshToken(jwt).isPresent();
    }

    @Override
    public UUID getUserIdFromAccessJwt(String jwt) {
        return decodeAccessToken(jwt)
                .map(token -> UUID.fromString(token.getSubject()))
                .orElse(null);
    }

    @Override
    public UUID getUserIdFromRefreshJwt(String jwt) {
        return decodeRefreshToken(jwt)
                .map(token -> UUID.fromString(token.getSubject()))
                .orElse(null);
    }

    @Override
    public UUID getTokenIdFromRefreshJwt(String jwt) {
        return decodeRefreshToken(jwt)
                //to prevent errors
                .filter(token ->
                        !(token.getClaim(TOKEN_ID_CLAIM).isMissing() || token.getClaim(TOKEN_ID_CLAIM).isNull()))
                .map(token -> UUID.fromString(token.getClaim(TOKEN_ID_CLAIM).asString()))
                .orElse(null);
    }

    @Override
    public String getUsernameFromAccessJwt(String jwt) {
        return decodeAccessToken(jwt)
                //ojo, si name claim no està en el access token -> claim as String: null
                .map(token -> token.getClaim(NAME_CLAIM).asString())
                .orElse(null);
    }

    @Override
    public Role getRoleFromAccessJwt(String jwt) {
        return decodeAccessToken(jwt)
                //ojo, si role claim no està en el access token -> claim as String: null -> Role.valueOf(null)!
                .filter(token -> !(token.getClaim(ROLE_CLAIM).isMissing() || token.getClaim(ROLE_CLAIM).isNull()))
               //Ara només s'executa el map si passa el filtre. Si no ha passat el filtre: Optional.empty
                .map(token -> Role.valueOf(token.getClaim(ROLE_CLAIM).asString()))
                .orElse(null);
    }

    private Optional<DecodedJWT> decodeAccessToken(String jwt) {
        return decode(jwt,accessTokenVerifier,"access");
    }

    private Optional<DecodedJWT> decodeRefreshToken(String jwt) {
        return decode(jwt,refreshTokenVerifier,"refresh");
    }

    private Optional<DecodedJWT> decode(String jwt, JWTVerifier verifier, String tokenType){
        try {
            return Optional.of(verifier.verify(jwt));
        } catch (JWTVerificationException e) {
            log.trace("no decoded "+tokenType+" token", e);
            return Optional.empty();
        }
    }
}
