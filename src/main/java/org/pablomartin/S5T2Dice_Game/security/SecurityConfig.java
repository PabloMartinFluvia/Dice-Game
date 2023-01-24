package org.pablomartin.S5T2Dice_Game.security;

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
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.savedrequest.RequestCacheAwareFilter;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.stereotype.Component;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder encoder (){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain loginChain(HttpSecurity http,
                                          @Qualifier("CustomBasicEntryPoint") AuthenticationEntryPoint entryPoint)
            throws Exception {

        SecurityFilterChain filterChain =   http
                .securityMatcher("/login")
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


    @Bean
    public SecurityFilterChain accessJwt(HttpSecurity http,
                                         @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
                                         @Qualifier("AccessProvider") AuthenticationProvider provider)
            throws Exception {
        return http
                .securityMatcher("/players/**")
                .csrf(csrf -> csrf.disable())
                .addFilterBefore(jwtFilter(provider), RequestCacheAwareFilter.class)
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/players").permitAll()
                        .requestMatchers("/players/credentials").hasRole(Role.ANNONIMUS.name())
                        //todo, personalize authorizations
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .formLogin(login -> login.disable())
                .build();
    }

    @Bean
    public SecurityFilterChain refreshJwt(HttpSecurity http,
                                         @Qualifier("JwtEntryPoint") AuthenticationEntryPoint entryPoint,
                                          @Qualifier("RefreshProvider") AuthenticationProvider provider)
            throws Exception {
        return http
                .securityMatchers(matchers -> matchers
                        .requestMatchers("/logout/**","/jwts/**"))
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

    /*
        the authentication manager used in the jwt's filters are the typical
        ProviderManager.
        Each only has one AuthentiactionProvider, wich tries to authenticate the unauthenticated
        Authentication generated by the filter
    */
    private Filter jwtFilter(AuthenticationProvider provider){
        AuthenticationManager manager = new ProviderManager(List.of(provider));
        return new JwtFilter(manager);
    }



}
