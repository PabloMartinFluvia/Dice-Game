package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for sing up.",
        description = "Allows to POST a new player. If body it's not provided player will sing up as ANONYMOUS.",
        requestBody = @RequestBody(
                description = "For sing up as REGISTERED: must contain valid username and password.",
                required = false,
                content = @Content(schema = @Schema(ref = "RegisteredPlayer"))
        ),
        responses = {
                @ApiResponse(
                        description = "On success: response provides the player's id and the jwts. If registered " +
                                "also includes username",
                        responseCode = "201",
                        content = @Content(examples = @ExampleObject(name = "singEx", ref = "SingUpExample"))),
                @ApiResponse(
                        description = "CONFLICT: Exists other player with the username provided.",
                        responseCode = "409",
                        content = @Content),
                @ApiResponse(
                        description = "BAD REQUEST: request body fails validations",
                        responseCode = "400",
                        content = @Content)}
)
@Target(METHOD)
@Retention(RUNTIME)
public @interface SingUpOperation {
}
