package org.pablomartin.S5T2Dice_Game.exceptions;

import java.util.UUID;

public class PlayerNotFoundException extends RuntimeException{

    public PlayerNotFoundException(UUID id){
        super("Doesn't exists any player with requested id: "+id.toString());
    }
}
