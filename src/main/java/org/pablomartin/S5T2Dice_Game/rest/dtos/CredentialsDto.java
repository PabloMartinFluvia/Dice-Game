package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.SchemaProperties;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.*;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Builder
@JsonInclude(NON_NULL)
@UsernameOrPassword(groups = UpdateCredentials.class)
@ToString
public class CredentialsDto {

    @JsonProperty(access = READ_ONLY)
    private UUID playerId;

    @NullableValidUsername (groups = {SetCredentials.class, UpdateCredentials.class})
    @NotNull(groups = SetCredentials.class)
    private String username;

    @JsonProperty(access = WRITE_ONLY) // for security
    @NullableValidPassword (groups = {SetCredentials.class, UpdateCredentials.class})
    @NotNull(groups = SetCredentials.class)
    private String password;

    @SchemaProperties
    @Schema
    @JsonProperty(access = READ_ONLY)
    private String accessJwt;


    @JsonProperty(access = READ_ONLY)
    private String refreshJwt;

    public CredentialsDto() {
        // for no args constructor when deserialization
    }

    //for builder
    private CredentialsDto(UUID playerId, String username, String password, String accessJwt, String refreshJwt) {
        this.playerId = playerId;
        this.username = username;
        this.password = password;
        this.accessJwt = accessJwt;
        this.refreshJwt = refreshJwt;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
