package org.pablomartin.S5T2Dice_Game.domain.services;

import com.auth0.jwt.JWT;


import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.JWTVerifier;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.RefreshToken;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

@Service
@PropertySource("classpath:values.properties")
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
        this.refreshTokenAlgorithm =Algorithm.HMAC256(accessTokenSecret);
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
    public String[] generateJwts(RefreshToken refreshToken) {
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

    public String generateRefreshJwt(RefreshToken refreshToken) {
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
}
