package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for log in.",
        description = "Allows authenticate a player with username/password. Anonymous players can't log in.",
        security = @SecurityRequirement(name = "basicAuth"),
        responses = {
                @ApiResponse(
                        description = "On success: response provides the player's id and the jwts.",
                        responseCode = "200",
                        content = @Content(examples = @ExampleObject(name = "logEx", ref = "LogInExample"))),
                @ApiResponse(
                        description = "UNAUTHORIZED: username:password unauthenticated.",
                        responseCode = "401",
                        content = @Content)
                 }
        )
@Target(METHOD)
@Retention(RUNTIME)
public @interface LoginOperation {
}
