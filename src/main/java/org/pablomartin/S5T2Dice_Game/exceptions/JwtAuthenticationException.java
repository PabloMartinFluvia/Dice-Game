package org.pablomartin.S5T2Dice_Game.exceptions;

import org.springframework.security.authentication.BadCredentialsException;

public class JwtAuthenticationException extends BadCredentialsException {

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}
