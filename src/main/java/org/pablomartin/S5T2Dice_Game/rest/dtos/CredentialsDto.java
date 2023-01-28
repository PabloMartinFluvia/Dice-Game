package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.*;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.WRITE_ONLY;

@Getter
@Setter
@UsernameOrPassword(groups = UpdateCredentials.class)
public class CredentialsDto {

    @NullableValidUsername (groups = {SetCredentials.class, UpdateCredentials.class})
    @NotNull(groups = SetCredentials.class)
    private String username;

    @NullableValidPassword (groups = {SetCredentials.class, UpdateCredentials.class})
    @NotNull(groups = SetCredentials.class)
    @JsonProperty(access = WRITE_ONLY) // for security
    private String password;
}
