package org.pablomartin.S5T2Dice_Game.security.principalsModels;

import jakarta.servlet.Filter;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;

import java.util.List;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    //1) Basic Filter -> basic authentication only aplied in login resource (AuthenticationsResources.class)

    //Bean UserDetailsService not declared here: PlayerDetailsService is @Service

    @Bean
    public PasswordEncoder encoder (){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain loginFilter(
            HttpSecurity http,
            @Qualifier("CustomBasicEntryPoint") AuthenticationEntryPoint entryPoint) throws Exception {
        SecurityFilterChain filterChain =   http
                .securityMatcher(LOGIN)
                .csrf(csrf -> csrf.disable())
                .httpBasic( auth -> auth.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
       /*
       TODO if possible: modify DaoAuthenticationProvider.setHideUserNotFoundExceptions(false);
       without declaring a new bean, I want to modify the default
        */
        return filterChain;
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
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(login -> login.disable())
                .logout(logout -> logout.disable()) //for access to my custom /logout resource
                .build();
    }

    //3) Admin filter -> authentication with Access JWT only apliet in admins/*** (SettingsResources.class)

    @Bean
    public SecurityFilterChain adminFilter(HttpSecurity http,
                                         @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
                                         @Qualifier("AccessProvider") AuthenticationProvider provider)
            throws Exception {
        return http
                .securityMatcher(ADMINS_ANY)
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        //.hasRole needs for work ex: "ADMIN" (role without prefix and maj.
                        //has role pareses role -> wiyh prefix and will check against authorities of Authentication
                        //.anyRequest().hasRole(Role.ADMIN.name()) //also Role.ADMIN {toString not specified in enum + enum don't have (abreviatures)
                        .anyRequest().hasAuthority(Role.ADMIN.withPrefix()) //more direct constrain
                )
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(login -> login.disable())
                .logout(logout -> logout.disable())
                .build();
    }

    //4) Access JWT for players

    @Bean
    public SecurityFilterChain accessJwtFilter(HttpSecurity http,
                                         @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
                                         @Qualifier("AccessProvider") AuthenticationProvider provider)
            throws Exception {
        return http
                .securityMatcher(PLAYERS_ANY)
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        //SettingsResources:
                        // singup, no need to be authenticated
                        .requestMatchers(HttpMethod.POST,PLAYERS).permitAll()
                        //update username and/or password
                        .requestMatchers(HttpMethod.PUT,PLAYERS).hasRole(Role.REGISTERED.toString())
                        //after anonymous singup wants to be registered providing username and password
                        .requestMatchers(PLAYERS_REGISTER).hasRole(Role.ANONYMOUS.toString())
                        //GameResources:
                        //with path variable id -> only authorized if the id of the authenticated user matches
                        .requestMatchers(PLAYERS_CONCRETE_ROLLS,PLAYERS_CONCRETE_RANKING)
                            .access(new WebExpressionAuthorizationManager("principal.userId.toString() == #id"))
                        //without path variable (statistics)
                        .anyRequest().authenticated()


                        .requestMatchers(HttpMethod.POST,"/players/{id}/games")
                        //works: al "llegir el id" del path de la petició -> tipus String
                            .access(new WebExpressionAuthorizationManager("principal.toString() == #id")))

                        //expresion can't be evaluated:
                        //.access(new WebExpressionAuthorizationManager("principal == UUID.fromString(#id)"))
                        //don't work
                        //.access(new WebExpressionAuthorizationManager("principal == #id"))
                        //don't work
                        //.access(new WebExpressionAuthorizationManager("principal.equals(#id)"))

                        /*
                        don't work:
                        Teoria:
                        If the evaluation context has been configured with a bean resolver it is possible to lookup
                        beans from an expression using the (@) symbol.
                        -> suposo que hi ha un problema amb el bean resolver
                         */
                        //.access(new WebExpressionAuthorizationManager("@webSecurity.check(authentication,#id)"))




                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(login -> login.disable())
                .logout(logout -> logout.disable())
                .build();
    }




    /*
        the authentication manager used in the jwt's filters are the typical ProviderManager.
        Each only has one AuthentiactionProvider.
    */
    private Filter jwtFilter(AuthenticationProvider provider){
        AuthenticationManager manager = new ProviderManager(List.of(provider));
        return new JwtFilter(manager);
    }
}
