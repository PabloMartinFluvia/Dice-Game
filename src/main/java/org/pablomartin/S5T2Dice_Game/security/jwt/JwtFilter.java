package org.pablomartin.S5T2Dice_Game.security.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.pablomartin.S5T2Dice_Game.exceptions.JwtAuthenticationException;
import org.pablomartin.S5T2Dice_Game.exceptions.ResolveBearerException;
import org.pablomartin.S5T2Dice_Game.security.jwt.headerResolver.BearerTokenResolver;
import org.pablomartin.S5T2Dice_Game.security.jwt.headerResolver.DefaultBearerTokenResolver;
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

    private final BearerTokenResolver resolver;

    private final AuthenticationManager manager;

    private AuthenticationDetailsSource<HttpServletRequest, ?> authenticationDetailsSource;

    private SecurityContextHolderStrategy securityContextHolderStrategy;

    private AuthenticationEntryPoint authenticationEntryPoint;

    private AuthenticationFailureHandler authenticationFailureHandler;

    private boolean ignoreFailure; // in case nexts filters in the chain could authenticate request



    public JwtFilter(AuthenticationManager manager){
        this.manager = manager;
        this.resolver = new DefaultBearerTokenResolver();
        this.authenticationDetailsSource = new WebAuthenticationDetailsSource();
        this.securityContextHolderStrategy = SecurityContextHolder.getContextHolderStrategy();
        this.authenticationEntryPoint = new JwtAuthenticationEntryPoint();
        this.authenticationFailureHandler = new AuthenticationEntryPointFailureHandler(
                (request, response, exception) ->
                    this.authenticationEntryPoint.commence(request, response, exception));
        this.ignoreFailure = false; //this is the only filter in the chain that can authenticate request
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            Optional<String> jwt = resolver.resolveTokenFromAuthorizationHeader(request);
            if (jwt.isEmpty()) {
                this.logger.trace("Did not process request since did not find a valid bearer jwt");
            }else {
                attemptAuthenticate(jwt.get(), request);
            }
            filterChain.doFilter(request,response);
        }catch (ResolveBearerException invalid){
            this.logger.trace("Failed to resolve bearer token", invalid);
            handleAuthenticationException(invalid, request, response, filterChain);
        }catch (JwtAuthenticationException failed){
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
        JwtAuthentication unverifiedAuthentication = new JwtAuthentication(jwt);
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
