package org.pablomartin.S5T2Dice_Game.exceptions;

public class AdminCredentialsException extends RuntimeException{

    public AdminCredentialsException() {
        super("ADMIN credentials can't be updated!");
    }
}
