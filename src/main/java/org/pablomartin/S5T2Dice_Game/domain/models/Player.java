package org.pablomartin.S5T2Dice_Game.domain.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.springframework.util.Assert;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player  {

    public static final String DEFAULT_USERNAME = "ANONIM";

    private UUID playerId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Role role;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime registerDate; //valor segons quan es crea

    public Player(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
        this.role = Role.REGISTERED;
    }

    public static Player defaultPlayer(){
        return Player.builder()
                .username(DEFAULT_USERNAME)
                .password(null)
                .role(Role.ANNONIMUS)
                .build();
    }

    public boolean hasUsernameToCheck(){
        return !(username ==null || username.equalsIgnoreCase(DEFAULT_USERNAME));
    }

    public void erasePassword() {
        this.password = "*******";
    }

    public void updateCredentials(Player source){
        Assert.isTrue(source.getPlayerId().equals(playerId),"Both ID must be equals.");
        Assert.isTrue(!role.equals(Role.ADMIN),"ADMIN credentials can't be updated!");
        String username = source.getUsername();
        String password = source.getPassword();
        if(username != null){
            this.username = username;
        }
        if(password != null){
            this.password = password;
        }
        if(username != null && password != null){
            this.role = Role.REGISTERED;
        }
    }
}
