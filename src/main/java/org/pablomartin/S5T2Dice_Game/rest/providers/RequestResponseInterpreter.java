package org.pablomartin.S5T2Dice_Game.rest.providers;

import org.pablomartin.S5T2Dice_Game.domain.models.Player;
import org.pablomartin.S5T2Dice_Game.domain.models.Roll;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AuthenticationInfoDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@PropertySource("classpath:values.properties")
public class RequestResponseInterpreter {

    private final PasswordEncoder encoder;

    public RequestResponseInterpreter(PasswordEncoder encoder) {
        this.encoder = encoder;
    }

    public Player parseCredentials(CredentialsDto credentialsDto){
        if(credentialsDto == null){
            //no body provided in request, only when singup an annonimus player
            return Player.builder().asAnnonimous().build();
        }else {
            String username = credentialsDto.getUsername();
            String password = credentialsDto.getPassword();
            password = password!=null ? encoder.encode(password) : null;
            /*
            username and/or password only can be null when a registered player wants to update credentials
                *  PUT /players   secured: hasRole registered
             */
            return Player.builder().asRegistered(username,password).build();
        }
    }

    public Roll parseRollDto(RollDto dto){
        return new Roll(dto.getDicesValues());
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

    public ResponseEntity<?> usernameResponse(Player player) {
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .playerId(player.getPlayerId())
                .username(player.getUsername())
                .build();
        return ResponseEntity.ok(accessInfo);
    }

    public ResponseEntity<?> jwtsResponse(Player player, String... jwts){
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .playerId(player.getPlayerId())
                .accessJwt(jwts[0])
                .refreshJwt(jwts[1])
                .build();
        return ResponseEntity.ok(accessInfo);
    }

    public ResponseEntity<?> accessJwtResponse(Player player, String accessJwt) {
        AuthenticationInfoDto accessInfo = AuthenticationInfoDto.builder()
                .playerId(player.getPlayerId())
                .accessJwt(accessJwt)
                .build();
        return ResponseEntity.ok(accessInfo);
    }

    public ResponseEntity<?> noContentResponse(){
        return ResponseEntity.noContent().build();
    }

}
