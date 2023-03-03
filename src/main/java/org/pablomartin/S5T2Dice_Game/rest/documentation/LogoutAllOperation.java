package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for logout all.",
        description = "Invalidates ALL refresh jwt linked to the authenticated user (the access ones will expire soon). \n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be REFRESH type.",
        security = @SecurityRequirement(name = "bearer-jwt"),
        responses = {
                @ApiResponse(
                        description = "On success: no content.",
                        responseCode = "204",
                        content = @Content),
                @ApiResponse(
                        description = "UNAUTHORIZED: not provided a valid bearer refresh jwt.",
                        responseCode = "401",
                        content = @Content)
        }
)
@Target(METHOD)
@Retention(RUNTIME)
public @interface LogoutAllOperation {
}
