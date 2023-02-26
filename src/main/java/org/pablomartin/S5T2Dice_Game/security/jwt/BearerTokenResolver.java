package org.pablomartin.S5T2Dice_Game.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import org.pablomartin.S5T2Dice_Game.exceptions.ResolveBearerException;

import java.util.Optional;

@FunctionalInterface
public interface BearerTokenResolver {

    Optional<String> resolveTokenFromAuthorizationHeader(HttpServletRequest request) throws ResolveBearerException;
}
