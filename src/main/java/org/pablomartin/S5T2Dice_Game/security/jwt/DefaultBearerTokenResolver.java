package org.pablomartin.S5T2Dice_Game.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import static org.pablomartin.S5T2Dice_Game.domain.services.JwtService.BEARER_;

import org.pablomartin.S5T2Dice_Game.exceptions.ResolveBearerException;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

import java.util.Optional;

public class DefaultBearerTokenResolver implements BearerTokenResolver {

    private final String BEARER_PREFIX = BEARER_;
    private final int JWT_DIMENSIONS = 3;

    @Override
    public Optional<String> resolveTokenFromAuthorizationHeader(HttpServletRequest request) throws ResolveBearerException {
        String jwt = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.startsWithIgnoreCase(jwt, BEARER_PREFIX)){
            return Optional.empty();
        }else if(jwt.split("\\.").length != JWT_DIMENSIONS){
            throw new ResolveBearerException("Bearer token is malformed");
        }else {
            return Optional.of(jwt.substring(BEARER_PREFIX.length()));
        }
    }
}
