package org.pablomartin.S5T2Dice_Game.domain.models.old;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext;
import org.pablomartin.S5T2Dice_Game.domain.models.Role;
import org.pablomartin.S5T2Dice_Game.exceptions.AdminOperationsException;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class PlayerOld {

    private UUID playerId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Role role;

    //@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime registerDate; //valor segons quan es crea

    public PlayerOld(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
        this.role = Role.REGISTERED;
    }

    public boolean hasUsernameToCheck(){
        //if true username value has been validated previously
        return username !=null && !username.equalsIgnoreCase(DiceGamePathsContext.getDefaultUsername());
    }

    public void erasePassword() {
        this.password = "*******";
    }

    public void updateCredentials(PlayerOld source){
        Assert.isTrue(this.playerId.equals(source.getPlayerId()),"Both ID must be equals.");
        if(this.role.equals(Role.ADMIN)){
            throw new AdminOperationsException("ADMIN credentials can't be updated!");
        }

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

    public static PlayerBuilder builder(){
        return new PlayerBuilder();
    }

    public static class PlayerBuilder {

        private PlayerOld playerOld;

        private PlayerBuilder(){
            //id field not inizialized, rest null
            playerOld = new PlayerOld();
            //player.setPlayerId(null);
            playerOld.setUsername(null);
            playerOld.setPassword(null);
            playerOld.setRole(null);
            playerOld.setRegisterDate(null);
        }

        public PlayerBuilder asAnnonimous(){
            //id field not inizialized, rest as annonimous singup
            //player.setPlayerId(null);
            playerOld.setUsername(DiceGamePathsContext.getDefaultUsername());
            playerOld.setRole(Role.ANONYMOUS);
            return this;
        }

        public PlayerBuilder asRegistered(String username, String password){
            playerOld.setUsername(username);
            playerOld.setPassword(password);
            playerOld.setRole(Role.REGISTERED);
            return this;
        }

        public PlayerBuilder playerId(UUID playerId){
            playerOld.setPlayerId(playerId);
            return this;
        }

        public PlayerBuilder username(String username){
            playerOld.setUsername(username);
            return this;
        }

        public PlayerBuilder password(String password){
            playerOld.setPassword(password);
            return this;
        }

        public PlayerBuilder role(Role role){
            playerOld.setRole(role);
            return this;
        }

        public PlayerBuilder registerDate(LocalDateTime registerDate){
            playerOld.setRegisterDate(registerDate);
            return this;
        }

        public PlayerOld build(){
            return playerOld;
        }
    }
}
