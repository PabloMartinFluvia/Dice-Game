package org.pablomartin.S5T2Dice_Game.domain.services.old;

import com.auth0.jwt.JWT;


import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.old.PlayerOld;
import org.pablomartin.S5T2Dice_Game.domain.models.old.Token;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.Role;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Service
@PropertySource("classpath:values.properties")
@Log4j2
public class DefaultJwtServiceOld implements JwtServiceOld {

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

    public DefaultJwtServiceOld(@Value("${jwt.issuer}") String issuer,
                                @Value("${jwt.access.expirationMinutes}") long accessTokenExpirationMinutes,
                                @Value("${jwt.refresh.expirationDays}") long refreshTokenExpirationDays,
                                @Value("${jwt.access.secret}") String accessTokenSecret,
                                @Value("${jwt.refresh.secret}") String refreshTokenSecret) {

        this.issuer = issuer;
        this.accessTokenExpirationMs = accessTokenExpirationMinutes*60*1000;
        this.refreshTokenExpirationMs = refreshTokenExpirationDays*24*60*60*1000;
        this.accessTokenAlgorithm =Algorithm.HMAC256(accessTokenSecret);
        this.refreshTokenAlgorithm =Algorithm.HMAC256(refreshTokenSecret);
        this.accessTokenVerifier = initAccessVerifier();
        this.refreshTokenVerifier = initRefreshVerifier();
    }

    private JWTVerifier initAccessVerifier(){
        return JWT
                .require(accessTokenAlgorithm)
                .withIssuer(issuer)
                //podira indicar més restriccions per a ser valid:
                //ex: que tingui un o N especific claim tingui un determinat valor
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
    public String[] generateJwts(Token refreshToken) {
        String accessJwt = generateAccessJwt(refreshToken.getOwner());
        String refreshJwt = generateRefreshJwt(refreshToken);
        return new String[]{accessJwt,refreshJwt};
    }


    @Override
    public String generateAccessJwt(PlayerOld playerOld){
        long now = System.currentTimeMillis();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(playerOld.getPlayerId())) //idem a ID_CLAIM;
                //.withClaim(NAME_CLAIM,player.getUsername())
                .withClaim(ROLE_CLAIM, playerOld.getRole().name())
                .withIssuedAt(new Date(now))
                .withNotBefore(new Date(now))
                .withExpiresAt(new Date(now + accessTokenExpirationMs))
                .sign(accessTokenAlgorithm);
    }

    private String generateRefreshJwt(Token refreshToken) {
        long now = System.currentTimeMillis();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(refreshToken.getOwner().getPlayerId()))
                .withClaim(TOKEN_ID_CLAIM, String.valueOf(refreshToken.getTokenId()))
                .withIssuedAt(new Date(now))
                .withNotBefore(new Date(now))
                .withExpiresAt(new Date(now + refreshTokenExpirationMs))
                .sign(refreshTokenAlgorithm);
    }

    @Override
    public boolean isValidAccessJwt(String jwt){
        return decodeAccessToken(jwt).isPresent();
    }

    @Override
    public boolean isValidRefreshJwt(String jwt){
        return decodeRefreshToken(jwt).isPresent();
    }

    @Override
    public UUID getUserIdFromAccesJwt(String jwt) {
        return decodeAccessToken(jwt)
                .map(token -> UUID.fromString(token.getSubject()))
                .orElse(null);

    }

    @Override
    public Role getUserRoleFormAccessJwt(String jwt) {
        return decodeAccessToken(jwt)
                .map(token -> Role.valueOf(token.getClaim(ROLE_CLAIM).asString()))
                .orElse(null);
    }

    @Override
    public UUID getUserIdFromRefreshJwt(String jwt) {
        return decodeRefreshToken(jwt)
                .map(token -> UUID.fromString(token.getSubject()))
                .orElse(null);

    }

    @Override
    public Set<String> getUserAuthoritiesFromAccesJwt(String jwt){
        //Role is included in token, there's no need to look for it in DB
        String role = decodeAccessToken(jwt)
                .map(token -> Role.PREFIX+token.getClaim(ROLE_CLAIM).asString())
                .orElse("");
        /*
        In this project there's no others authorities (such READ, WRITE, MODIFY_ROLLS) granted
        -> there's no need to look for them in DB
         */
        return Set.of(role);
    }


    @Override
    public UUID getTokenIdFromRefreshJwt (String jwt){
        return decodeRefreshToken(jwt)
                .map(token -> UUID.fromString(token.getClaim(TOKEN_ID_CLAIM).asString()))
                .orElse(null);
    }


    private Optional<DecodedJWT> decodeAccessToken(String jwt) {
        return decode(jwt,accessTokenVerifier,"corrupted access token");
    }

    private Optional<DecodedJWT> decodeRefreshToken(String jwt) {
        return decode(jwt,refreshTokenVerifier,"corrupted refresh token");
    }

    private Optional<DecodedJWT> decode(String jwt, JWTVerifier verifier, String message){
        try {
            return Optional.of(verifier.verify(jwt));
        } catch (JWTVerificationException e) {
            log.trace(message, e);
            return Optional.empty();
        }
    }
}
