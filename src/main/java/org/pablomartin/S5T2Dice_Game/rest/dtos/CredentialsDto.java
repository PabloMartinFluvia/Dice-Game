package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.util.UUID;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;
import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Builder
@JsonInclude(NON_NULL)
@UsernameOrPassword(groups = UpdateCredentials.class)
public class CredentialsDto {

    private UUID playerId;

    @NullableValidUsername (groups = {SetCredentials.class, UpdateCredentials.class})
    @NotNull(groups = SetCredentials.class)
    private String username;

    @NullableValidPassword (groups = {SetCredentials.class, UpdateCredentials.class})
    @NotNull(groups = SetCredentials.class)
    @JsonProperty(access = WRITE_ONLY) // for security
    private String password;

    private String accessJwt;

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
