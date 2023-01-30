package org.pablomartin.S5T2Dice_Game.rest.providers;

import org.pablomartin.S5T2Dice_Game.domain.models.DetailsJwt;
import org.pablomartin.S5T2Dice_Game.security.basic.PlayerDetails;
import org.pablomartin.S5T2Dice_Game.security.jwt.RefreshTokenDetails;

/*
Responsibility:
Provide models to services from input dtos or authentication's principal.
 */
public interface ModelsProvider {

    /**
     * Note: PlayerDetails doesn't provide the role directly, must be filtered
     * from the granted authorities (inherited from UserDetails)
     * @param details
     * @return
     */
    DetailsJwt fromBasicPrincipal(PlayerDetails details);

    /**
     * Note: RefreshTokenDetails (Authentication's principal when authenticated
     * with a refresh token) can provide the role directly (but not the full
     * authorities, if are needed should be loaded from the Authentication stored
     * in the security context).
     * @param details
     * @return
     */
    DetailsJwt fromRefreshPrincipal(RefreshTokenDetails details);
}
