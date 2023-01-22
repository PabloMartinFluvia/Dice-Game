package org.pablomartin.S5T2Dice_Game.domain.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@EqualsAndHashCode
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Player  {

    private UUID playerId;

    private String username;

    private String password;

    private Role role;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime registerDate; //valor segons quan es crea

    public Player(String default_username){
        this.username = default_username;
        this.password = null;
        this.role = Role.ANNONIMUS;
    }

    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.role = Role.REGISTERED;
    }

    public boolean isAnnonimus(){
        return role.equals(Role.ANNONIMUS);
    }

    public void erasePassword() {
        this.password = "*******";
    }

}
