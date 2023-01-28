package org.pablomartin.S5T2Dice_Game.domain.models;

import jakarta.validation.constraints.NotBlank;
import lombok.*;
import org.pablomartin.S5T2Dice_Game.exceptions.AdminCredentialsException;
import org.springframework.util.Assert;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
@ToString
public class Player  {

    private UUID playerId;

    @NotBlank
    private String username;

    @NotBlank
    private String password;

    private Role role;

    //@JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime registerDate; //valor segons quan es crea

    public Player(String username, String encodedPassword) {
        this.username = username;
        this.password = encodedPassword;
        this.role = Role.REGISTERED;
    }

    public boolean hasUsernameToCheck(){
        //if true username value has been validated previously
        return username !=null && !username.equalsIgnoreCase(DiceGameContext.getDefaultUsername());
    }

    public void erasePassword() {
        this.password = "*******";
    }

    public void updateCredentials(Player source){
        Assert.isTrue(this.playerId.equals(source.getPlayerId()),"Both ID must be equals.");
        if(this.role.equals(Role.ADMIN)){
            throw new AdminCredentialsException();
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

        private Player player;

        private PlayerBuilder(){
            //id field not inizialized, rest null
            player = new Player();
            //player.setPlayerId(null);
            player.setUsername(null);
            player.setPassword(null);
            player.setRole(null);
            player.setRegisterDate(null);
        }

        public PlayerBuilder asAnnonimous(){
            //id field not inizialized, rest as annonimous singup
            //player.setPlayerId(null);
            player.setUsername(DiceGameContext.getDefaultUsername());
            player.setRole(Role.ANONYMOUS);
            return this;
        }

        public PlayerBuilder asRegistered(String username, String password){
            player.setUsername(username);
            player.setPassword(password);
            player.setRole(Role.REGISTERED);
            return this;
        }

        public PlayerBuilder playerId(UUID playerId){
            player.setPlayerId(playerId);
            return this;
        }

        public PlayerBuilder username(String username){
            player.setUsername(username);
            return this;
        }

        public PlayerBuilder password(String password){
            player.setPassword(password);
            return this;
        }

        public PlayerBuilder role(Role role){
            player.setRole(role);
            return this;
        }

        public PlayerBuilder registerDate(LocalDateTime registerDate){
            player.setRegisterDate(registerDate);
            return this;
        }

        public Player build(){
            return player;
        }
    }
}
