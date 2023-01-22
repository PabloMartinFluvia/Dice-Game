package org.pablomartin.S5T2Dice_Game.domain.services;

import com.auth0.jwt.JWT;


import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Token;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
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
public class DefaultJwtService implements JwtService{

    public static final String BEARER_ = "Bearer ";

    private static final String ROLE_CLAIM = "role";

    private static final String NAME_CLAIM = "username";

    private static final String TOKEN_ID_CLAIM = "tokenId";

    private final String issuer;

    private final long accessTokenExpirationMs; //en milisegons

    private final long refreshTokenExpirationMs; //en milisegons
    private final Algorithm accessTokenAlgorithm;

    private final Algorithm refreshTokenAlgorithm;

    private JWTVerifier accessTokenVerifier;

    private JWTVerifier refreshTokenVerifier;

    public DefaultJwtService(@Value("${jwt.issuer}") String issuer,
                      @Value("${jwt.access.expirationMinutes}") long accessTokenExpirationMinutes,
                      @Value("${jwt.refresh.expirationDays}") long refreshTokenExpirationDays,
                      @Value("${jwt.access.secret}") String accessTokenSecret,
                      @Value("${jwt.refresh.secret}") String refreshTokenSecret) {

        this.issuer = issuer;
        this.accessTokenExpirationMs = accessTokenExpirationMinutes*60*1000;
        this.refreshTokenExpirationMs = refreshTokenExpirationDays*24*60*60*1000;
        this.accessTokenAlgorithm =Algorithm.HMAC256(accessTokenSecret);
        this.refreshTokenAlgorithm =Algorithm.HMAC256(refreshTokenSecret);
        initVerifiers();
    }

    private void initVerifiers(){
        this.accessTokenVerifier = JWT
                .require(accessTokenAlgorithm)
                .withIssuer(issuer)
                //podira indicar més restriccions per a ser valid:
                //ex: que tingui un o N especific claim tingui un determinat valor
                .build();
        this.refreshTokenVerifier = JWT
                .require(refreshTokenAlgorithm)
                .withIssuer(issuer)
                .build();
    }

    @Override
    public String[] generateJwts(Token refreshToken) {
        String accessJwt = generateAccessJwt(refreshToken.getOwner());
        String refreshJwt = generateRefreshJwt(refreshToken);
        return new String[]{accessJwt,refreshJwt};
    }


    public String generateAccessJwt(Player player){
        long now = System.currentTimeMillis();
        return JWT.create()
                .withIssuer(issuer)
                .withSubject(String.valueOf(player.getPlayerId())) //idem a ID_CLAIM;
                .withClaim(NAME_CLAIM,player.getUsername())
                .withClaim(ROLE_CLAIM,player.getRole().name())
                .withIssuedAt(new Date(now))
                .withNotBefore(new Date(now))
                .withExpiresAt(new Date(now + accessTokenExpirationMs))
                .sign(accessTokenAlgorithm);
    }

    public String generateRefreshJwt(Token refreshToken) {
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
        try {
            return Optional.of(accessTokenVerifier.verify(jwt));
        } catch (JWTVerificationException e) {
            log.error("corrupted access token", e);
        }
        return Optional.empty();
    }

    private Optional<DecodedJWT> decodeRefreshToken(String jwt) {
        try {
            return Optional.of(refreshTokenVerifier.verify(jwt));
        } catch (JWTVerificationException e) {
            log.trace("corrupted refresh token", e);
            return Optional.empty();
        }
    }
}
