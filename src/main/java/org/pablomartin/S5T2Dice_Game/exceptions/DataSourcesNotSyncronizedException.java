package org.pablomartin.S5T2Dice_Game.exceptions;

//if extends runtime exception, the transaction will roll back when it's trowed
public class DataSourcesNotSyncronizedException extends RuntimeException{

    public DataSourcesNotSyncronizedException (String msg){
        super(msg);
    }
}
