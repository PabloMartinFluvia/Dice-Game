package org.pablomartin.S5T2Dice_Game.rest;

import org.springframework.security.core.context.SecurityContextHolder;

public interface SecuredResource {

    default  <T> T loadPrincipal(Class<T> type){
        return (T) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
