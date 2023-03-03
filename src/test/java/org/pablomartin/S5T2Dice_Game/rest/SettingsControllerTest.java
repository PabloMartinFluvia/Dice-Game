package org.pablomartin.S5T2Dice_Game.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.annotation.Before;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pablomartin.S5T2Dice_Game.domain.models.InfoForAppAccess;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.services.AccessService;
import org.pablomartin.S5T2Dice_Game.domain.services.GameService;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Set;

import static java.rmi.server.LogStream.log;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.PLAYERS;

@ExtendWith(SpringExtension.class)  //magrate the test to Junit5

//@WebMvcTest //Majoria de beans deshabilitat, mirar docs classe. Or @SpringBootTest + @AutoConfigureMockMvc
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:test.properties")
@Log4j2
class SettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SettingsResources controller;

    @Autowired
    private Validator validator; // injecto, per a que s'injecti @Value en els validators

    @MockBean
    private AccessService service;


    @MockBean
    private ModelsProvider models;

    @MockBean
    private ResponsesProvider responses;

    @Autowired
    private ObjectMapper mapper;



    @Test
    public void singUpTest() throws Exception {

        /*
        Funcionaria: pero com al deserialitzar el password té access write only ->
            passoword no en json -> input dto no valid
        CredentialsDto dto = CredentialsDto.builder().username("aaaaa").password("mf002828").build();
        String credentials =mapper
                .writeValueAsString(dto);
        log.info(credentials);
        */
        String username = "aaaaa";
        String password = "123456789";
        //String credentials = "{\"username\": \"aaaaa\", \"password\" : \"mf002828\"}";
        String credentials = "{\"username\": \""+username+"\", \"password\" : \""+password+"\"}";



        Player player = Player.builder().build();
        when(models.fromCredentials(any(CredentialsDto.class))).thenReturn(player);
        when(service.performSingUp(any(NewPlayerInfo.class))).thenReturn(player);
        /*
        ResponseEntity<?> genera problemes degut al generic:
        Opcio 1) Treure el generic;
        ResponseEntity response = ResponseEntity.status(HttpStatus.CREATED).body(player);
        given(responses.forSingUp(player)).willReturn(response);
        Opcio 2)
        doReturn ... when(mock).method
         */
        //doReturn(ResponseEntity.status(HttpStatus.CREATED).body(player)).when(responses).forSingUp(player);
        when(responses.forSingUp(any(InfoForAppAccess.class))).thenReturn(ResponseEntity.status(HttpStatus.CREATED).body(null));

        mockMvc.perform(
                        MockMvcRequestBuilders.post(PLAYERS)
                        .content(credentials)
                        .contentType(MediaType.APPLICATION_JSON)
                ).andExpect(
                        MockMvcResultMatchers
                                .status()
                                .isCreated());
    }

    @Test
    public void dtoConstraintTest(){

        //this doesn't load spring context -> no hi ha injeccio en els validators
        //Validator validator = Validation.buildDefaultValidatorFactory().getValidator();


        String username = "holaquetal000";
        String password = "mf002828....";

        CredentialsDto dto = CredentialsDto.builder().username(username).password(password).build();
        log.info("--->"+dto);
        Set<ConstraintViolation<CredentialsDto>> violations = validator.validate(dto, SetCredentials.class);
        for(ConstraintViolation<CredentialsDto> violation : violations){
            log.info("-->"+violation.getMessage());
        }

        assertTrue(violations.isEmpty(), "Some violations found");

        username = null;
        dto = CredentialsDto.builder().username(username).password(password).build();
        assertFalse(validator.validate(dto, SetCredentials.class).isEmpty(), "no violations found");
        assertTrue(validator.validate(dto, UpdateCredentials.class).isEmpty(), "some violations found");

        password = null;
        dto = CredentialsDto.builder().username(username).password(password).build();
        assertFalse(validator.validate(dto, SetCredentials.class).isEmpty(), "no violations found");
        assertFalse(validator.validate(dto, UpdateCredentials.class).isEmpty(), "no violations found");
    }
}