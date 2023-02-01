package org.pablomartin.S5T2Dice_Game.rest;

import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AccessDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.BasicCredentials;
import org.pablomartin.S5T2Dice_Game.domain.services.AccessService;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.SetCredentials;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UpdateCredentials;
import org.pablomartin.S5T2Dice_Game.rest.providers.ModelsProvider;
import org.pablomartin.S5T2Dice_Game.rest.providers.ResponsesProvider;
import org.pablomartin.S5T2Dice_Game.security.jwt.TokenPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

import static org.pablomartin.S5T2Dice_Game.domain.models.DiceGameContext.*;

@RestController
@RequestMapping
@RequiredArgsConstructor
public class SettingsController implements SettingsResoruces{

    private final ModelsProvider models;

    private final AccessService service;

    private final ResponsesProvider responses;



    //ANY ALLOWED

    @PostMapping(path = PLAYERS)
    @Override
    public ResponseEntity<?> singUp(
            @RequestBody(required = false) @Validated(SetCredentials.class) CredentialsDto dto) {
        BasicCredentials basicCredentials = models.fromCredentials(dto);
        AccessDetails accessDetails = service.performSingUp(basicCredentials);
        return responses.forSingUp(accessDetails);
    }

    // ACCESS JWT AUTHENTICATION + ROLE ANONYMOUS

    @PutMapping(path = PLAYERS_REGISTER)
    @Override
    public ResponseEntity<?> registerAnonymous(
            @RequestBody @Validated(SetCredentials.class) CredentialsDto dto,
            @AuthenticationPrincipal TokenPrincipal principal) {
        AccessDetails accessDetails = updateCredentials(principal,dto);
        return responses.forRegisterAnonymous(accessDetails);
    }

    // ACCESS JWT AUTHENTICATION + ROLE REGISTERED

    @PutMapping(path = PLAYERS)
    @Override
    public ResponseEntity<?> updateRegistered(
            @RequestBody @Validated(UpdateCredentials.class) CredentialsDto dto,
            @AuthenticationPrincipal TokenPrincipal principal){
        AccessDetails accessDetails = updateCredentials(principal,dto);
        return responses.forUpdateRegistered(accessDetails);
    }

    private AccessDetails updateCredentials(TokenPrincipal principal, CredentialsDto dto){
        UUID playerId = principal.getOwnerId();
        BasicCredentials basicCredentials = models.fromCredentials(dto);
        return service.updateCredentials(playerId,basicCredentials);
    }

    // ACCESS JWT AUTHENTICATION + ROLE ADMIN

    @DeleteMapping(path = ADMINS_PLAYERS_CONCRETE)
    @Override
    public ResponseEntity<?> deleteUser(@PathVariable("id") UUID targetNotAdminUserId) {
        service.deleteUser(targetNotAdminUserId);
        return responses.fromDeleteUser();
    }

    @PutMapping(path = ADMINS_PLAYERS_CONCRETE_PROMOTE)
    @Override
    public ResponseEntity<?> promoteRegisteredUser(@PathVariable("id") UUID targetRegisteredUserId) {
        service.promoteUser(targetRegisteredUserId);
        return responses.fromPromoteUser();
    }
}
