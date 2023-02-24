package org.pablomartin.S5T2Dice_Game.domain.models;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.UUID;

@Builder
@Getter
@ToString
@EqualsAndHashCode
public class PlayerSecurity {

    private String passwordEncoded;

    private Role role;

    //no others authorities needed

    private UUID refreshTokenId; //only one refreshtoken Id is needed

    private String accessJwt, refreshJwt;

    public void setAccessJwt(String accessJwt) {
        this.accessJwt = accessJwt;
    }

    public void setRefreshJwt(String refreshJwt) {
        this.refreshJwt = refreshJwt;
    }
}
