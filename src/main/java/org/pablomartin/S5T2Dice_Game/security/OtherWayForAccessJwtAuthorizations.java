package org.pablomartin.S5T2Dice_Game.security;

import jakarta.servlet.Filter;
import jakarta.servlet.http.HttpServletRequest;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.security.jwt.JwtFilter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authorization.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.List;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

//@Configuration //for testing purposes
public class OtherWayForAccessJwtAuthorizations {

    //@Bean
    public SecurityFilterChain accessJwtFilterV2(HttpSecurity http,
            @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
            @Qualifier("AccessProvider") AuthenticationProvider provider,
            @Qualifier("jwtAccessAuthorization") AuthorizationManager<RequestAuthorizationContext> authManager)
            throws Exception {
        return http
                .securityMatcher(PLAYERS_ANY)
                .csrf(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .authorizeHttpRequests(authorize -> authorize

                        //.requestMatchers("/s").hasRole("s") //un no definit en el autoritzador

                        //podria injectar N authoritzadors
                        //.requestMatchers("/d").access(accessManager3)
                        //.requestMatchers("/d").access(accessManager2)

                        .anyRequest().access(authManager))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .build();
    }

    private Filter jwtFilter(AuthenticationProvider provider){
        AuthenticationManager manager = new ProviderManager(List.of(provider));
        return new JwtFilter(manager);
    }


    //@Bean("jwtAccessAuthorization")
    public AuthorizationManager<RequestAuthorizationContext> requestMatcherAuthorizationManager
        (HandlerMappingIntrospector introspector) {

        //preparar builder per a crear request matchers
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        //Creo tants RequestMatchers com tipus de condicions de seguretat tingui

        //  *Un RequestMatcher pot estar associat a N paths si vui
        RequestMatcher permitAll = mvcMatcherBuilder.pattern(HttpMethod.POST,PLAYERS);
        RequestMatcher roleRegistered = mvcMatcherBuilder.pattern(HttpMethod.PUT,PLAYERS);
        RequestMatcher roleAnonymous = mvcMatcherBuilder.pattern(PLAYERS_REGISTER);
        RequestMatcher idInPathMatches =
                new AndRequestMatcher(
                        mvcMatcherBuilder.pattern(PLAYERS_CONCRETE_ROLLS),
                        mvcMatcherBuilder.pattern(PLAYERS_CONCRETE_RANKING));
        RequestMatcher justAuthenticated = AnyRequestMatcher.INSTANCE;

        RequestMatcher testRoleHierarchy = mvcMatcherBuilder.pattern("/players/test/hierarchy");
        RequestMatcher testAuthManager = mvcMatcherBuilder.pattern("/players/test/authManager");


        //Creo un authorization manager (delegador) segons la petició rebuda <HttpServletRequest>
        //Aquest delegador  te K,V; que son els RequestsMatchers definits prèviament,
        //associant-los a un AuthorizationManager<RequestAuthorizationContext>
        //  *Els AuthorizationManager<RequestAuthorizationContext> són els que accepta el .access()
        // *Puc passar-los en format lambda, ja que son FI
        AuthorizationManager<HttpServletRequest> manager = RequestMatcherDelegatingAuthorizationManager
                .builder()
                //ordre importa!!!!!

                //allow all
                .add(permitAll, (authenticationSupplier,context) -> new AuthorizationDecision(true))

                //role registered condition
                .add(roleRegistered, AuthorityAuthorizationManager.hasRole(Role.REGISTERED.toString()))
                //role anonymous condition
                .add(roleAnonymous, AuthorityAuthorizationManager.hasRole(Role.VISITOR.toString()))

                //one role, but authorization manager has set a role hierarchy
                //so a superior role is also allowed
                .add(testRoleHierarchy, specificRoleOrSuperior(Role.REGISTERED)) // -> anonymous denied / registered and admin authorized

                //authorization restriction based on an expression
                .add(idInPathMatches,SecurityConfig.idAuthorizer())

                //specify restriction with static methods
                // *there's also allOF -> AND condition and others
                .add(testAuthManager, AuthorizationManagers.anyOf( //OR
                        AuthorityAuthorizationManager.hasAuthority(Role.VISITOR.withPrefix()),
                        AuthorityAuthorizationManager.hasAuthority(Role.ADMIN.withPrefix())))

                // authenticated restriction
                .add(justAuthenticated, new AuthenticatedAuthorizationManager<>())
                .build();

        //** to do: check others implementations

        //Retornar FI d'un AuthenticationManagerRequestAuthorizationContext>
        //per a check li demano al Delegador definit que checkegi la petició
        return (authenticationSupplier, requestAuthorizationContext)
                -> manager.check(authenticationSupplier,requestAuthorizationContext.getRequest());
    }

    private AuthorizationManager <RequestAuthorizationContext> specificRoleOrSuperior(Role role){
        AuthorityAuthorizationManager<RequestAuthorizationContext> manager
                = AuthorityAuthorizationManager.hasRole(role.toString());
        manager.setRoleHierarchy(roleHierarchy());
        return manager;
    }


    private RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(Role.ADMIN.withPrefix()+" > "+Role.REGISTERED.withPrefix()+"\n" +
                Role.REGISTERED.withPrefix()+" > "+Role.VISITOR.withPrefix());
        return hierarchy;
    }



}
