package org.pablomartin.S5T2Dice_Game.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static org.pablomartin.S5T2Dice_Game.domain.services.JwtService.BEARER_;

@Component("JwtEntryPoint")
@Log4j2
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final String BEARER_PREFIX = BEARER_;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, BEARER_PREFIX);
        response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
        log.trace("------"+authException.getMessage()+", "+authException.getClass().getSimpleName());
    }
}
