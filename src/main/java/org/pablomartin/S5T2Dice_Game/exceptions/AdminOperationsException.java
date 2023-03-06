package org.pablomartin.S5T2Dice_Game.exceptions;

public class AdminOperationsException extends RuntimeException{ //Illegal operation on an admin

    public AdminOperationsException(String msg) {
        super(msg);
    }
}
