package org.pablomartin.S5T2Dice_Game.rest.providers;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotNull;
import org.pablomartin.S5T2Dice_Game.domain.models.BasicCredentials;
import org.pablomartin.S5T2Dice_Game.domain.models.JwtOwnerDetails;
import org.pablomartin.S5T2Dice_Game.domain.models.RollDetails;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.pablomartin.S5T2Dice_Game.security.basic.PlayerPrincipalDetails;
import org.pablomartin.S5T2Dice_Game.security.jwt.RefreshTokenPrincipal;

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
     * @param dto
     * @return If dto it's not provided (username: default + password: null). If it's
     * provided (username: provided or null if not provided + password: encoded or null if not provided)
     */
    BasicCredentials fromCredentials(@Nullable CredentialsDto dto);

    //AUTHENTICATION CONTROLLER

    /**
     * Note: PlayerDetails doesn't provide the role directly, must be filtered
     * from the granted authorities (inherited from UserDetails)
     * @param details
     * @return
     */
    JwtOwnerDetails fromBasicPrincipal(@NotNull PlayerPrincipalDetails details);

    /**
     * Note: RefreshTokenDetails (Authentication's principal when authenticated
     * with a refresh token) can provide the role directly (but not the full
     * authorities, if are needed should be loaded from the Authentication stored
     * in the security context).
     * @param details
     * @return
     */
    JwtOwnerDetails fromRefreshPrincipal(@NotNull RefreshTokenPrincipal details);

    //GAME CONTROLLER

    /**
     * Parses the dices values stored in the dto to an
     * instance of RollDetails.
     * @param dto
     * @return
     */
    RollDetails fromRoll(@NotNull RollDto dto);
}
