package org.pablomartin.S5T2Dice_Game.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.pablomartin.S5T2Dice_Game.exceptions.ResolveBearerException;
import org.springframework.security.authentication.AuthenticationDetailsSource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextHolderStrategy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationEntryPointFailureHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Optional;


public class JwtFilter extends OncePerRequestFilter {

    private final BearerTokenResolver requestResolver;

    private final AuthenticationManager manager;

    private final AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private final SecurityContextHolderStrategy securityContextHolderStrategy;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final AuthenticationFailureHandler authenticationFailureHandler;

    private boolean ignoreFailure; // in case next filters in the chain could authenticate request



    public JwtFilter(AuthenticationManager manager){
        this.manager = manager;
        this.requestResolver = new DefaultBearerTokenResolver();
        this.authenticationDetailsSource = new WebAuthenticationDetailsSource();
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();

        this.authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        this.authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(authenticationEntryPoint);

        this.ignoreFailure = false; //this is the only filter in the chain that can authenticate request
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<String> jwt = requestResolver.resolveTokenFromAuthorizationHeader(request);
            if (jwt.isEmpty()) {
                this.logger.trace("Request not processed due not find a valid bearer jwt");
            }else {
                attemptAuthenticate(jwt.get(), request);
            }
            filterChain.doFilter(request,response);
        }catch (ResolveBearerException invalid){ //-> AuthenticationException
            this.logger.trace("Failed to resolve bearer token from header", invalid);
            handleAuthenticationException(invalid, request, response, filterChain);
        }catch (JwtAuthenticationException failed){ //-> BadCredentialsException extends AuthenticationException
            this.logger.trace("Failed to process authentication request", failed);
            handleAuthenticationException(failed, request, response, filterChain);
        }
    }

    private void handleAuthenticationException(AuthenticationException ex, HttpServletRequest request,
                                               HttpServletResponse response,FilterChain filterChain) throws ServletException, IOException {
        this.securityContextHolderStrategy.clearContext();
        if (this.ignoreFailure) {
            filterChain.doFilter(request, response);
        }
        else {
            this.authenticationEntryPoint.commence(request, response, ex);
            //Idem:
            //this.authenticationFailureHandler.onAuthenticationFailure(request,response,ex);
        }
    }


    private void attemptAuthenticate(String jwt, HttpServletRequest request) throws JwtAuthenticationException{
        JwtAuthentication unverifiedAuthentication = JwtAuthentication.asUnauthenticated(jwt);
        unverifiedAuthentication.setDetails(this.authenticationDetailsSource.buildDetails(request));
        Authentication authentication = manager.authenticate(unverifiedAuthentication);
        saveAuthentication(authentication);
    }

    private void saveAuthentication(Authentication authentication){
        SecurityContext context = this.securityContextHolderStrategy.createEmptyContext();
        context.setAuthentication(authentication);
        this.securityContextHolderStrategy.setContext(context);

        //alternativa més light:
        //SecurityContextHolder.getContext().setAuthentication(authentication);
    }

}
