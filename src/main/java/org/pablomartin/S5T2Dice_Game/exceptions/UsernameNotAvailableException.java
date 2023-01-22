package org.pablomartin.S5T2Dice_Game.exceptions;

public class UsernameNotAvailableException extends RuntimeException{

    public UsernameNotAvailableException (String username){
        super("Already exists another player registered with this username: "+username+".");
    }
}
