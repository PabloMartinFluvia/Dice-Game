package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for reset the JWTs.",
        description = "Invalidates the previous refresh jwts (the access ones will expire soon). \n\n" +
                "Also generates new access and refresh jwts. \n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be REFRESH type.",
        security = @SecurityRequirement(name = "bearer-jwt"),
        responses = {
                @ApiResponse(
                        description = "On success: response provides news access and refresh JWTs.",
                        responseCode = "200",
                        content = @Content(examples = @ExampleObject(name = "resEx", ref = "LogInExample"))),
                @ApiResponse(
                        description = "UNAUTHORIZED: not provided a valid bearer refresh jwt.",
                        responseCode = "401",
                        content = @Content)
        }
)
@Target(METHOD)
@Retention(RUNTIME)
public @interface ResetOperation {
}
