package org.pablomartin.S5T2Dice_Game.rest.interpreters;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AccessInfoDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.LoginDto;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:values.properties")
public class RequestResponseInterpreter {

    private final String DEFAULT_USERNAME = "ANONIM";

    private final PasswordEncoder encoder;

    public RequestResponseInterpreter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public Player toPlayer(LoginDto loginDto){
        if(loginDto == null){
            //no body provided in request
            return new Player(DEFAULT_USERNAME);
        }else {
            //username and password provided
            return new Player(loginDto.getUsername(), encoder.encode(loginDto.getPassword()));
        }
    }

    public ResponseEntity<?> singupResponse(Player player, String... jwts){
        AccessInfoDto accessInfo = AccessInfoDto.builder()
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
        AccessInfoDto accessInfo = AccessInfoDto.builder()
                .playerId(player.getPlayerId())
                .accessJwt(jwts[0])
                .refreshJwt(jwts[1])
                .build();
        return ResponseEntity.ok(accessInfo);
    }

    public ResponseEntity<?> okResponse(){
        return ResponseEntity.ok().build();
    }
}
