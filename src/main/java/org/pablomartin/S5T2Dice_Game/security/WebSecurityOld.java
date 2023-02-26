package org.pablomartin.S5T2Dice_Game.security;

import org.pablomartin.S5T2Dice_Game.security.principalsModels.TokenPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Objects;
import java.util.UUID;

@Component
public class WebSecurityOld {



    public boolean check(Authentication authentication, String idInPath){
        Assert.isInstanceOf(TokenPrincipal.class,authentication.getPrincipal(),"Authentication's principal must be a Token Principal");
        UUID fromPath = UUID.fromString(idInPath);
        UUID fromAuthentication =((TokenPrincipal) authentication.getPrincipal()).getUserId();
        return Objects.equals(fromAuthentication,fromPath);
    }
}
