package org.pablomartin.S5T2Dice_Game.rest.providers;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.pablomartin.S5T2Dice_Game.domain.models.*;

import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.security.basic.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.principalsModels.RefreshTokenPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

@Component
@RequiredArgsConstructor
public class DefaultModelsProvider implements ModelsProvider {

    private final PasswordEncoder encoder;

    @Override
    public NewPlayerInfo fromCredentials(@Nullable CredentialsDto dto) {
        if(dto == null){
            //body not provided in request, only when sing up an anonymous player
            return Player.asAnnonimous();
        }else {
            /*
            if it's not null:
            Option A: provided by an anonymous user who wants to register (dto full populated)
            Option B: provided by a registered user who wants to update username and/or password
             */
            Assert.isTrue(dto.getUsername()!= null || dto.getPassword()!= null,
                    "CredentialsDto must contain at least a valid username or password.");
            String passwordEncoded =  dto.getPassword() == null ? null : encoder.encode(dto.getPassword());
            return Player.asRegistered(dto.getUsername(),passwordEncoded);
        }
    }

    @Override
    public SecurityClaims fromBasicPrincipal(@NotNull BasicPrincipal principal) {
        return Player.builder()
                .playerId(principal.getUserId())
                .username(principal.getUsername())
                .security(PlayerSecurity.builder()
                        .role(principal.getUserRole())
                        .build())
                .build();
    }

    @Override
    public SecurityClaims fromRefreshPrincipal(@NotNull RefreshTokenPrincipal principal) {
        return Player.builder()
                .playerId(principal.getUserId())
                .username(principal.getUsername())
                .security(PlayerSecurity.builder()
                        .role(principal.getUserRole())
                        .build())
                .build();
    }

    @Override
    public RollDetails fromRoll(@NotNull RollDto dto) {
        return Roll.builder()
                .dicesValues(dto.getDicesValues())
                .build();
    }
}
