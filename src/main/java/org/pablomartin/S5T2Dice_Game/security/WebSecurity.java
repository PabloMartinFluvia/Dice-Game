package org.pablomartin.S5T2Dice_Game.security;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebSecurity {

    public boolean check(Authentication authentication, String id){
        String playerId = authentication.getPrincipal().toString();
        return playerId .equals( id);
    }
}
