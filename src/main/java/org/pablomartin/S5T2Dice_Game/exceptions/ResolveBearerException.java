package org.pablomartin.S5T2Dice_Game.exceptions;

import org.springframework.security.core.AuthenticationException;

public class ResolveBearerException extends AuthenticationException {
    public ResolveBearerException(String msg) {
        super(msg);
    }
}
