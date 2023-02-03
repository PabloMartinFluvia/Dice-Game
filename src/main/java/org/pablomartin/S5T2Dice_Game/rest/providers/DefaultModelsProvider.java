package org.pablomartin.S5T2Dice_Game.rest.providers;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.ProvidedCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.DefaultCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.credentials.AuthenticationCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.game.DefaultRoll;
import org.pablomartin.S5T2Dice_Game.domain.models.game.RollDetails;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.security.basic.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.jwt.RefreshTokenPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class DefaultModelsProvider implements ModelsProvider {

    private final PasswordEncoder encoder;

    @Override
    public ProvidedCredentials fromCredentials(@Nullable CredentialsDto dto) {
        if(dto == null){
            //body not provided in request, only when sing up an anonymous player
            return DefaultCredentials.builder()
                    .asAnnonimous()
                    .build();
        }else {
            /*
            if it's not null:
            Option A: provided by an anonymous user who wants to register (dto full populated)
            Option B: provided by a registered user who wants to update username and/or password
             */
            Assert.isTrue(dto.getUsername()!= null || dto.getPassword()!= null,
                    "CredentialsDto must contain at least a valid username or password.");
            String encodedPassword =  dto.getPassword()!=null ? encoder.encode(dto.getPassword()) : null;
            return DefaultCredentials.builder()
                    .asRegistered(dto.getUsername(),encodedPassword)
                    .build();
        }
    }

    @Override
    public AuthenticationCredentials fromBasicPrincipal(@NotNull BasicPrincipal principal) {
        return DefaultCredentials.builder()
                .playerId(principal.getPlayerId())
                .username(principal.getUsername())
                .role(principal.getRole())
                .build();
    }

    @Override
    public AuthenticationCredentials fromRefreshPrincipal(@NotNull RefreshTokenPrincipal principal) {
        return DefaultCredentials.builder()
                .playerId(principal.getOwnerId())
                .username(principal.getOwnerUsername())
                .role(principal.getOwnerRole())
                .build();
    }

    @Override
    public RollDetails fromRoll(@NotNull RollDto dto) {
        return DefaultRoll.builder()
                .dicesValues(dto.getDicesValues())
                .build();
    }
}
