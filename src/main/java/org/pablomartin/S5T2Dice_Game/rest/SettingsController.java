package org.pablomartin.S5T2Dice_Game.rest;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.InfoForAppAccess;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.services.AccessService;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.TokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SettingsController implements SettingsResources {

    private final ModelsProvider models;

    private final AccessService service;

    private final ResponsesProvider responses;



    //ANY ALLOWED

    @PostMapping(path = PLAYERS)
    @Override
    public ResponseEntity<?> singUp(
            @RequestBody(required = false) @Validated(SetCredentials.class) CredentialsDto dto) {
        NewPlayerInfo credentials = models.fromCredentials(dto);
        InfoForAppAccess infoForAppAccess = service.performSingUp(credentials);
        return responses.forSingUp(infoForAppAccess);
    }

    // ACCESS JWT AUTHENTICATION + ROLE ANONYMOUS

    @PutMapping(path = PLAYERS_REGISTER)
    @Override
    public ResponseEntity<?> registerAnonymous(
            @RequestBody @Validated(SetCredentials.class) CredentialsDto dto,
            @AuthenticationPrincipal TokenPrincipal principal) {
        InfoForAppAccess infoForAppAccess = updateCredentials(principal,dto);
        return responses.forRegisterAnonymous(infoForAppAccess);
    }

    // ACCESS JWT AUTHENTICATION + ROLE REGISTERED

    @PutMapping(path = PLAYERS)
    @Override
    public ResponseEntity<?> updateRegistered(
            @RequestBody @Validated(UpdateCredentials.class) CredentialsDto dto,
            @AuthenticationPrincipal TokenPrincipal principal){
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
    @Override
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID targetNotAdminUserId) {
        service.deleteUser(targetNotAdminUserId);
        return responses.fromDeleteUser();
    }
}
