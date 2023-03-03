package org.pablomartin.S5T2Dice_Game.utils;

import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.examples.Example;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.*;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.io.Resource;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.LOGIN;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@PropertySource("classpath:values.properties")
@Log4j2
public class DocsConfig {

    @Value("${application.version}")
    private String appVersion;

    @Value("${application.description}")
    private String appDescription;

    @Value("${server.port}")
    private String serverPort;

    private final String developerName = "Pablo Martin FLuvia";

    private final String developerEmail = "paumf00@gmail.com";

    private final String appTitle = "Dice Game App";

    private final String hostPath = "http://localhost:";

    private final String serverDescription = "Application's server.";

    @Bean
    public OpenAPI documentation(
            @Qualifier("postPlayerSchema") Schema postPlayer,
            @Qualifier("postRollSchema") Schema postRoll,
            @Qualifier("singupExample") Example singupResponse,
            @Qualifier("loginExample") Example loginResponse,
            @Qualifier("accessExample") Example accessResponse,
            @Qualifier("newRollExample") Example newRollResponse,
            @Qualifier("allRollsExample") Example allRollsResponse,
            @Qualifier("winrateExample") Example winRateResponse,
            @Qualifier("rankingExample") Example rankingResponse){

    return new OpenAPI()
                .info(new Info()
                        .title(appTitle)
                        .version(appVersion)
                        .description(appDescription)
                        .contact(new Contact()
                                .name(developerName)
                                .email(developerEmail)))
                .components(new Components()
                        .addSchemas("RegisteredPlayer",postPlayer)
                        .addSchemas("PostNewRoll",postRoll)
                        .addExamples("SingUpExample", singupResponse)
                        .addExamples("LogInExample",loginResponse)
                        .addExamples("AccessExample",accessResponse)
                        .addExamples("NewRollExample",newRollResponse)
                        .addExamples("AllRollsExample",allRollsResponse)
                        .addExamples("WinRateExample",winRateResponse)
                        .addExamples("RankingExample",rankingResponse)
                        .addSecuritySchemes("basicAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("basic"))
                         .addSecuritySchemes("bearer-jwt",
                                 new SecurityScheme()
                                         .type(SecurityScheme.Type.HTTP)
                                         .scheme("bearer")
                                         .bearerFormat("JWT"))
                        )
                .addServersItem(new Server() // if not specified it's autodetected (ok),  but with a default server's description.
                        .url(hostPath+serverPort)
                        .description(serverDescription))
                //* .openapi : default specification version 3.0
                // .paths -> autodetected and customized with annotations


                ;
    }


    @Bean
    public Schema postPlayerSchema(
            @Value("${player.username.length.min}") int minUsername,
            @Value("${player.username.length.max}") int maxUsername,
            @Value("${player.password.length.min}") int minPassword,
            @Value("${player.password.length.max}") int maxPassword) {

        String username = "username";
        String password = "password";

        return new ObjectSchema() //type object, format null
                .required(List.of(username,password))
                .addProperty(username,new StringSchema()
                        .minLength(minUsername)
                        .maxLength(maxUsername)
                        .example("username_registered"))
                .addProperty(password,new StringSchema()
                        .minLength(minPassword)
                        .maxLength(maxPassword)
                        .example("password_registered"));
    }

    @Bean
    public Schema postRollSchema(
            @Value("${dices.numRequired}") int numDices,
            @Value("${dices.value.max}") int maxValue,
            @Value("${dices.value.min}") int minValue){

        String dices = "dicesValues";
        return new ObjectSchema()
                .required(Collections.singletonList(dices))
                .addProperty(dices, new ArraySchema()
                        .minItems(numDices)
                        .maxItems(numDices)
                        .items(new IntegerSchema()
                                .maximum(BigDecimal.valueOf(maxValue))
                                .minimum(BigDecimal.valueOf(minValue))));

    }

    @Bean
    public Example singupExample(
            @Value("classpath:/examples/SingUp.txt") Resource postPlayer){
        return new Example()
                .summary("Response when post new player.")
                .value(asString(postPlayer));
    }

    @Bean
    public Example loginExample(
            @Value("classpath:/examples/LogIn.txt") Resource login){
        return new Example()
                .summary("Response with jwts.")
                .value(asString(login));
    }

    @Bean
    public Example accessExample(
            @Value("classpath:/examples/Access.txt") Resource access){
        return new Example()
                .summary("Response for new Access JWT.")
                .value(asString(access));
    }

    @Bean
    public Example newRollExample(
            @Value("classpath:/examples/NewRoll.txt") Resource newRoll){
        return new Example()
                .summary("Response for post new roll.")
                .value(asString(newRoll));
    }

    @Bean
    public Example allRollsExample(
            @Value("classpath:/examples/AllRolls.txt") Resource allRolls){
        return new Example()
                .summary("Response for get all rolls.")
                .value(asString(allRolls));
    }

    @Bean
    public Example winrateExample(
            @Value("classpath:/examples/WinRate.txt") Resource winrate){
        return new Example()
                .summary("Response for get win rate.")
                .value(asString(winrate));
    }

    @Bean
    public Example rankingExample(
            @Value("classpath:/examples/Ranking.txt") Resource ranking){
        return new Example()
                .summary("Response for get all players.")
                .value(asString(ranking));
    }

    private String asString(Resource resource) {
        try (Reader reader = new InputStreamReader(resource.getInputStream())) {
            return FileCopyUtils.copyToString(reader);
        }
        catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    /*
    not needed, ja que no hi ha cap security filter chain que defineixi la seguretat
    per a paths docs
     */
    //@Bean
    public SecurityFilterChain swagger(HttpSecurity http) throws Exception {
        return   http
                //.securityMatcher("/swagger-ui/**", "/v3/api-docs/**")
                .securityMatcher("/docs/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest().permitAll())
                .httpBasic(withDefaults())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}

