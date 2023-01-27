package org.pablomartin.S5T2Dice_Game.security;

import jakarta.servlet.http.HttpServletRequest;
import org.aopalliance.intercept.MethodInvocation;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authorization.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.WebExpressionAuthorizationManager;
import org.springframework.security.web.access.intercept.RequestAuthorizationContext;
import org.springframework.security.web.access.intercept.RequestMatcherDelegatingAuthorizationManager;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.security.web.util.matcher.AndRequestMatcher;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;

import java.util.UUID;

public class Extra {
    //-------------------------------------------------------------------------

    //@Bean
    SecurityFilterChain web(HttpSecurity http, AuthorizationManager<RequestAuthorizationContext> accessManager)
            throws Exception {
        http
                .securityMatcher("/blabla") //segons els RequestMatchers mapejats
                .authorizeHttpRequests((authorize) -> authorize
                        //per a qualsevol petitició que ho consulti al autoritzador
                        .requestMatchers("/s").hasRole("s") //un no definit en el autoritzador
                        //.requestMatchers("/d").access(accessManager3)
                        //.requestMatchers("/d").access(accessManager2)

                        .anyRequest().access(accessManager)
                );

        return http.build();
    }

    //@Bean
    AuthorizationManager<RequestAuthorizationContext> requestMatcherAuthorizationManager
    (HandlerMappingIntrospector introspector) {

        //preparar builder per a crear request matchers
        MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);

        //Creo tants RequestMatcher com tipus de codicions de seguretat tingui
        //  *Un RequestMatcher pot estar associat a N paths si vui
        RequestMatcher permitAll =
                new AndRequestMatcher(
                        mvcMatcherBuilder.pattern("/resources/**"),
                        mvcMatcherBuilder.pattern("/signup"),
                        mvcMatcherBuilder.pattern("/about"));

        RequestMatcher adminRole = mvcMatcherBuilder.pattern("/admin/**");
        RequestMatcher dbRole = mvcMatcherBuilder.pattern(HttpMethod.GET,"/db/**");
        RequestMatcher customContition = mvcMatcherBuilder.pattern("/xx/**");
        RequestMatcher idemCustomContition = mvcMatcherBuilder.pattern("/zz/**");
        RequestMatcher authenticatedAnyNotSpecified = AnyRequestMatcher.INSTANCE;



        //Creo un authorization manager (delegador) segons la petició rebuda <HttpServletRequest>
        //Aquest delegador  te K,V; que son els RequestsMatchers definits previaments,
        //associant-los a un AuthorizationManager<RequestAuthorizationContext>
        //  *Els AuthorizationManager<RequestAuthorizationContext> són els que accepta el .access()
        // *Puc passar-los en format lambda, ja que son FI
        AuthorizationManager<HttpServletRequest> manager = RequestMatcherDelegatingAuthorizationManager
                .builder()
                //ordre importa!!!!!

                //allow all
                .add(permitAll, (authenticationSuplier,context) -> new AuthorizationDecision(true))

                //one role
                .add(adminRole, AuthorityAuthorizationManager.hasRole("ADMIN"))

                //one role, but authorization manager has setted a role hieracy
                //so a superior role is also allowed
                .add(dbRole, registeredRoleAuthorizationOrSuperior())

                //authorization restriction based on a expression
                .add(customContition,new WebExpressionAuthorizationManager
                        ("hasRole('ADMIN') and hasRole('DBA')"))

                //idem restriction, but especified as a AND of authorization managers
                // *there's also anyOf -> OR condition
                .add(idemCustomContition, AuthorizationManagers.allOf(
                        AuthorityAuthorizationManager.hasRole("ADMIN"),
                        AuthorityAuthorizationManager.hasRole("DBA")))

                // authenticated restriction
                .add(authenticatedAnyNotSpecified, new AuthenticatedAuthorizationManager())
                .build();

        //** to do: check others implementations

        //Retornar FI d'un AuthenticationManagerRequestAuthorizationContext>
        //per a check li demano al Delegador definit que checkeji la peticio
        return (authenticationSuplier, requestAuthorizationContext)
                -> manager.check(authenticationSuplier,requestAuthorizationContext.getRequest());
    }

    private AuthorizationManager <RequestAuthorizationContext> registeredRoleAuthorizationOrSuperior(){
        AuthorityAuthorizationManager<RequestAuthorizationContext> manager
                = AuthorityAuthorizationManager.hasRole(Role.REGISTERED.toString());
        manager.setRoleHierarchy(roleHeracy());
        return manager;
    }


    private RoleHierarchy roleHeracy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy(Role.ADMIN.withPrefix()+" > "+Role.REGISTERED.withPrefix()+"\n" +
                Role.REGISTERED.withPrefix()+" > "+Role.ANNONIMUS.withPrefix());
        return hierarchy;
    }


    /*
    Per autoritzar segons expresions
    Utilitzant un WebEx`ressionAuthorizationManager
    Exemple: quan hi ha una petició que matches -> se li demana al WebExpressionAuthorizationManager
    que evalui la expressió -> en aquest cas crida al mètode de la classe (ha de ser BEAN) especificat
    + el paràmetre amb # l'agafa del path
    El mètode ha de retornar true or false -> segons el resultat s'autoritzarà o no
            ** mètode compara el id del path amb el id de la authentication
     */
    //@Component
    public class webSecurity {
        public boolean checkUserId(Authentication accesJwt, UUID id) {
            return accesJwt.getPrincipal().equals(id);
        }
    }


    public SecurityFilterChain test2(HttpSecurity http) throws Exception {
        return http.
                authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/user/{userId}/**")
                        .access(new WebExpressionAuthorizationManager
                                ("@securityConfig.checkUserId(authentication,#userId)"))
                ).build();
    }
}
