package org.pablomartin.S5T2Dice_Game.security;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
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

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder encoder (){
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain chainJwt(HttpSecurity http) throws Exception {
        return http
                .securityMatchers(matchers -> matchers
                        .requestMatchers("/players/**", "/tokens/**"))
                .csrf(csrf -> csrf.disable())
                //TODO filtre jwt
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(HttpMethod.POST,"/players").permitAll()
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                //TODO custom entry point
                .formLogin(login -> login.disable())
                .build();
    }

    @Bean
    public SecurityFilterChain chainLogin(HttpSecurity http,
                                          @Qualifier("CustomBasicEntryPoint") AuthenticationEntryPoint entryPoint)
            throws Exception {

        SecurityFilterChain filterChain =   http
                .securityMatcher("/login")
                .csrf(csrf -> csrf.disable())
                .httpBasic( auth -> auth.authenticationEntryPoint(entryPoint))
                //.httpBasic(Customizer.withDefaults()) // default entry point ok
                //.exceptionHandling(handler -> handler.authenticationEntryPoint(entryPoint))
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().authenticated())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .formLogin(login -> login.disable())
                .build();

       /*
       TODO: modify DaoAuthenticationProvider.setHideUserNotFoundExceptions(false);
       without declaring a new bean, I want to modify the default
        */


        return filterChain;
    }



}
