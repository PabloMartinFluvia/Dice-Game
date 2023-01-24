package org.pablomartin.S5T2Dice_Game.domain.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@Builder
public class Player  {

    public static final String DEFAULT_USERNAME = "ANONIM";

    private UUID playerId;

    private String username;

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

    public boolean isAnnonimus(){
        return role.equals(Role.ANNONIMUS);
    }

    public void erasePassword() {
        this.password = "*******";
    }

}
