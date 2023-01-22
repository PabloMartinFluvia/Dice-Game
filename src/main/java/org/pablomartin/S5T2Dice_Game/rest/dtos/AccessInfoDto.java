package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

@Getter
@Setter
@Builder
public class AccessInfoDto {

    private UUID playerId;

    @JsonInclude(NON_NULL)
    private String username;

    private String accessJwt;

    private String refreshJwt;

}
