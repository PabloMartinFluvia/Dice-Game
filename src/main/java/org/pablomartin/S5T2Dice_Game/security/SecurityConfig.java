package org.pablomartin.S5T2Dice_Game.security;

import jakarta.servlet.Filter;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.AuthorizationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    //1) Basic Filter -> basic authentication only applied in login resource (AuthenticationsResources.class)

    //Bean UserDetailsService not declared here: PlayerDetailsService is @Service

    @Bean
    public PasswordEncoder encoder (){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain loginFilter(
            HttpSecurity http,
            @Qualifier("CustomBasicEntryPoint") AuthenticationEntryPoint entryPoint) throws Exception {
        return   http
                .securityMatcher(LOGIN)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic( auth -> auth.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
       /*
       TODO if possible: modify DaoAuthenticationProvider.setHideUserNotFoundExceptions(false);
       without declaring a new bean, I want to modify the default
        */
    }

    //2) Refresh jwt filter -> authentication with refresh JWT only applied in logout/** and jwt/** (AuthenticationsResources.class)

    @Bean
    public SecurityFilterChain refreshJwtFilter(HttpSecurity http,
                                          @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
                                          @Qualifier("RefreshProvider") AuthenticationProvider provider)
            throws Exception {
        return http
                .securityMatchers(matchers -> matchers
                        .requestMatchers(LOGOUT_ANY,JWTS_ANY))
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable) //for access to my custom /logout resource
                .build();
    }

    //3) Admin filter -> authentication with Access JWT only applied in admins/*** (SettingsResources.class)

    @Bean
    public SecurityFilterChain adminFilter(HttpSecurity http,
                                         @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
                                         @Qualifier("AccessProvider") AuthenticationProvider provider)
            throws Exception {
        return http
                .securityMatcher(ADMINS_ANY)
                .cors(AbstractHttpConfigurer::disable)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        //.hasRole needs for work ex: "ADMIN" (role without prefix and maj.
                        //has role pareses role -> with prefix and will check against authorities of Authentication
                        //.anyRequest().hasRole(Role.ADMIN.name()) //also Role.ADMIN {toString not specified in enum + enum don't have (abreviatures)
                        .anyRequest().hasAuthority(Role.ADMIN.withPrefix()) //more direct constrain
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    //4) Access JWT for players

    @Bean
    public SecurityFilterChain accessJwtFilter(HttpSecurity http,
                                         @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
                                         @Qualifier("AccessProvider") AuthenticationProvider provider)
            throws Exception {
        //AnonymousAuthenticationFilter
        return http
                .securityMatcher(PLAYERS_ANY)
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        //SettingsResources:
                        // sing up, no need to be authenticated
                        .requestMatchers(HttpMethod.POST,PLAYERS).permitAll()
                        //update username and/or password
                        .requestMatchers(HttpMethod.PUT,PLAYERS).hasRole(Role.REGISTERED.toString())
                        //after anonymous sing up wants to be registered providing username and password
                        .requestMatchers(PLAYERS_REGISTER).hasRole(Role.VISITOR.toString())
                        //GameResources:
                        //with path variable id -> only authorized if the id of the authenticated user matches
                        .requestMatchers(PLAYERS_CONCRETE_ROLLS,PLAYERS_CONCRETE_RANKING).access(idAuthorizer())
                        //without path variable (statistics)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }




    /*
        the authentication manager used in the jwt's filters are the typical ProviderManager.
        Each only has one AuthenticationProvider.
    */
    private Filter jwtFilter(AuthenticationProvider provider){
        AuthenticationManager manager = new ProviderManager(List.of(provider));
        return new JwtFilter(manager);
    }


    //PATH VARIABLE CHECKS

    //https://docs.spring.io/spring-security/reference/5.8/servlet/authorization/expression-based.html
    public static AuthorizationManager<RequestAuthorizationContext> idAuthorizer(){
        String expression;
        //expression = "principal.userId.toString() == #id"; //field userId not found, only works with "well known properties"

        //expression = "principal.toString() == #id"; // worked (DefaultPrincipal toString returns userId stored)

        expression = "authentication.name == #id"; //worked (JwtAuthentication getName returns string userId if not null and if principal is Token Principal)
                                                            //otherwise calls super.getName

        return new WebExpressionAuthorizationManager(expression);
    }

    /*
    Not working with bean method ->?

    Teoria:
    If the evaluation context has been configured with a bean resolver it is possible to lookup
    beans from an expression using the (@) symbol.
    -> suposo que hi ha un problema amb el bean resolver
     */

    public AuthorizationManager<RequestAuthorizationContext> notWorkingBean(){
        return new WebExpressionAuthorizationManager
                ("@webSecurity.checkUserId(authentication,#id)");
    }



    /*
    OLD situation;
    when principal of the JwtAuthentication  was directly the user ID
        Object principal = playerId
        *when authenticated with an AccessJWT
     */
    private AuthorizationManager<RequestAuthorizationContext> oldExpressions(){

        String expression;
        expression = "principal.toString() == #id"; // worked
        //expression = "principal == #id"; // not worked -> id in path is read as string
        //expression = "principal.equals(#id)"; // not worked -> id in path is read as string
        //expression = "principal == UUID.fromString(#id)"; // expression CAN'T be evaluated

        return new WebExpressionAuthorizationManager(expression);
    }
}
