package org.pablomartin.S5T2Dice_Game.rest.providers;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.NewPlayerInfo;
import org.pablomartin.S5T2Dice_Game.domain.models.SecurityClaims;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.security.basic.BasicPrincipal;
import org.pablomartin.S5T2Dice_Game.security.jwt.providers.RefreshTokenPrincipal;

/*
Responsibility:
Provide models to services from input dtos or authentication's principal.
So services can operate with objects that are independent of
how Authentication's Principal are stored or the dto's are provided.
 */
public interface ModelsProvider {

    //SETTINGS CONTROLLER

    /**
     * Parses the basic credentials stored in the dto to an
     * instance of BasicCredentials.
     * @param dto request body
     * @return If dto it's not provided (username: default + password: null). If it's
     * provided (username: provided or null if not provided + password: encoded or null if not provided)
     */
    NewPlayerInfo fromCredentials(@Nullable CredentialsDto dto);

    //AUTHENTICATION CONTROLLER

    /**
     * Note: BasicPrincipal must be configured to provide the role directly.
     * @param principal of Authentication
     * @return model with input info
     */
    SecurityClaims fromBasicPrincipal(@NotNull BasicPrincipal principal);

    /**
     * Note: RefreshTokenPrincipal (Authentication's principal when authenticated
     * with a refresh token) can provide the role directly (but not the full
     * authorities, if are needed should be loaded from the Authentication stored
     * in the security context).
     * @param principal of Authentication
     * @return model with input info
     */
    SecurityClaims fromRefreshPrincipal(@NotNull RefreshTokenPrincipal principal);

    //GAME CONTROLLER

    /**
     * Parses the dices values stored in the dto to an
     * instance of RollDetails.
     * @param dto request body
     * @return model with input info
     */
    RollDetails fromRoll(@NotNull RollDto dto);
}
