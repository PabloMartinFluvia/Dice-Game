package org.pablomartin.S5T2Dice_Game.rest;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.pablomartin.S5T2Dice_Game.domain.models.InfoForAppAccess;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.services.AccessService;
import org.pablomartin.S5T2Dice_Game.rest.documentation.DeleteUserOperation;
import org.pablomartin.S5T2Dice_Game.rest.documentation.RegisterUserOperation;
import org.pablomartin.S5T2Dice_Game.rest.documentation.SingUpOperation;
import org.pablomartin.S5T2Dice_Game.rest.documentation.UpdateUserOperation;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.DefaultPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.TokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

@RestController
@RequestMapping(produces = "application/json")
@Tag(name = "App access settings resources.")
@RequiredArgsConstructor
@Log4j2
public class SettingsController implements SettingsResources {

    private final ModelsProvider models;

    private final AccessService service;

    private final ResponsesProvider responses;


    //ANY ALLOWED

    @PostMapping(path = PLAYERS)
    @SingUpOperation
    public ResponseEntity<?> singUp(
            @RequestBody(required = false) @Validated(SetCredentials.class) CredentialsDto dto) {
        NewPlayerInfo credentials = models.fromCredentials(dto);
        InfoForAppAccess infoForAppAccess = service.performSingUp(credentials);
        return responses.forSingUp(infoForAppAccess);
    }

    /*
    @AuthenticationPrincipal as an argument:
    If type is a concrete class there's no problem (ex: DefaultPrincipal.class).
    In this project I want to work with interfaces, the more, the better (even with models).
    In theory shouldn't be any problem, they say it works with UserDetails and OAuth2User (interfaces).
    And it's true, /login with @AuthenticationPrincipal UserDetails principal works,
    BUT if I change to @AuthenticationPrincipal BasicPrincipal principal NOT WORKS
        *Note: BasicPrincipal extends UserDetails
        Argument it's loaded as an empty proxy.
        Issue (unresolved) disscussed here:
        https://github.com/spring-projects/spring-security/issues/10930
     */

    // ACCESS JWT AUTHENTICATION + ROLE ANONYMOUS

    @PutMapping(path = PLAYERS_REGISTER)
    @RegisterUserOperation
    public ResponseEntity<?> registerAnonymous(
            @RequestBody @Validated(SetCredentials.class) CredentialsDto dto) {

        TokenPrincipal principal = loadPrincipal(TokenPrincipal.class);

        InfoForAppAccess infoForAppAccess = updateCredentials(principal,dto);
        return responses.forRegisterAnonymous(infoForAppAccess);
    }

    // ACCESS JWT AUTHENTICATION + ROLE REGISTERED

    @PutMapping(path = PLAYERS)
    @UpdateUserOperation
    public ResponseEntity<?> updateRegistered(
            @RequestBody @Validated(UpdateCredentials.class) CredentialsDto dto){

        TokenPrincipal principal = loadPrincipal(TokenPrincipal.class);

        InfoForAppAccess infoForAppAccess = updateCredentials(principal,dto);
        return responses.forUpdateRegistered(infoForAppAccess);
    }

    private InfoForAppAccess updateCredentials(TokenPrincipal principal, CredentialsDto dto){
        NewPlayerInfo credentials = models.fromCredentials(dto);
        credentials.setPlayerId(principal.getUserId());
        return service.updateCredentials(credentials);
    }

    // ACCESS JWT AUTHENTICATION + ROLE ADMIN

    @DeleteMapping(path = ADMINS_PLAYERS_CONCRETE)
    @DeleteUserOperation
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID targetNotAdminUserId) {
        service.deleteUser(targetNotAdminUserId);
        return responses.fromDeleteUser();
    }
}
