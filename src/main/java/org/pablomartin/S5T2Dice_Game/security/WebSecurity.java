package org.pablomartin.S5T2Dice_Game.security;

import org.pablomartin.S5T2Dice_Game.security.jwt.providers.TokenPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.UUID;

@Component("webSecurity")
public class WebSecurity {

    public boolean checkUserId(Authentication authentication, String idInPath){
        Assert.isInstanceOf(TokenPrincipal.class,authentication.getPrincipal(),"Authentication's principal must be a Token Principal");
        UUID fromPath = UUID.fromString(idInPath);
        UUID fromAuthentication =((TokenPrincipal) authentication.getPrincipal()).getUserId();
        return Objects.equals(fromAuthentication,fromPath);
    }
}
