package org.pablomartin.S5T2Dice_Game.exceptions;

//if extends runtime exception, the transaction will roll back when it's trowed
public class DataSourcesNotSynchronizedException extends RuntimeException{

    public DataSourcesNotSynchronizedException(String msg){
        super(msg);
    }
}
