package org.pablomartin.S5T2Dice_Game.security.basic;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component("CustomBasicEntryPoint")
@Log4j2
public class CustomBasicAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Basic realm=");
        response.sendError(HttpStatus.UNAUTHORIZED.value(), authException.getMessage());
        log.trace("------"+authException.getMessage()+", "+authException.getClass().getSimpleName());

        //BadCretentialsException -> password erroni
    }
}
