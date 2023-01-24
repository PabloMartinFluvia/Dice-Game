package org.pablomartin.S5T2Dice_Game.rest.interpreters;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AuthenticationInfoDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.BasicCredentialsDto;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@PropertySource("classpath:values.properties")
public class RequestResponseInterpreter {

    private final PasswordEncoder encoder;

    public RequestResponseInterpreter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public Player toPlayer(BasicCredentialsDto basicCredentialsDto){
        if(basicCredentialsDto == null){
            //no body provided in request, only when singup
            return Player.defaultPlayer(); //unregistered
        }else {
            //dto provided
            String username = null;
            if(basicCredentialsDto.getUsername() != null){
                username = basicCredentialsDto.getUsername();
            }

            String password = null;
            if(basicCredentialsDto.getPassword() != null){
                password = encoder.encode(basicCredentialsDto.getPassword());
            }
            //-> registered,
            //    ** only both/any arguments can be null when a registered player wants to update credentials
            //      from PUT /players   secured: hasRole registered
            return new Player(username, password);
        }
    }

    public ResponseEntity<?> singupResponse(Player player, String... jwts){
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .playerId(player.getPlayerId())
                .username(player.getUsername())
                .accessJwt(jwts[0])
                .refreshJwt(jwts[1])
                .build();
        //TODO: add uri location in response
        //return ResponseEntity.created(Uri location).body(accessInfo);
        return ResponseEntity.status(HttpStatus.CREATED).body(accessInfo);
    }

    public ResponseEntity<?> loginResponse(Player player, String... jwts){
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .playerId(player.getPlayerId())
                .accessJwt(jwts[0])
                .refreshJwt(jwts[1])
                .build();
        return ResponseEntity.ok(accessInfo);
    }

    public ResponseEntity<?> usernameResponse(Player player) {
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .playerId(player.getPlayerId())
                .username(player.getUsername())
                .build();
        return ResponseEntity.ok(accessInfo);
    }

    public ResponseEntity<?> noContentResponse(){
        return ResponseEntity.noContent().build();
    }

    public ResponseEntity<?> accessJwtResponse(Player player, String accessJwt) {
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .playerId(player.getPlayerId())
                .accessJwt(accessJwt)
                .build();
        return ResponseEntity.ok(accessInfo);
    }

    public ResponseEntity<?> resetJwtResponse(String[] jwts) {
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .accessJwt(jwts[0])
                .refreshJwt(jwts[1])
                .build();
        return ResponseEntity.ok(accessInfo);
    }



}
