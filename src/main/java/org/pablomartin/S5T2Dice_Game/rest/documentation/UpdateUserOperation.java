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
        summary = "Request for modify player's credentials.",
        description = "Allows to modify player's username and/or password. \n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be ACCESS type. Authorized only if REGISTERED.",
        security = @SecurityRequirement(name = "bearer-jwt"),
        requestBody = @RequestBody(
                description = "Must contain username AND/OR password (at least one field valid for update).",
                required = true,
                content = @Content(schema = @Schema(ref = "RegisteredPlayer"))
        ),
        responses = {
                @ApiResponse(
                        description = "On success: response returns player's id + " +
                                "if username updated: new username " +
                                "and new access jwt (previous ones won't work anymore). ",
                        responseCode = "200",
                        content = @Content(examples = {
                                @ExampleObject(
                                        name = "With username provided for update.",
                                        value ="{\"playerId\": \"player_uuid\", " +
                                                "\"username\": \"username_provided\", " +
                                                "\"accessJwt\": \"new_access_jwt\"}"),
                                @ExampleObject(
                                        name = "Username not provided, neither updated.",
                                        value ="{\"playerId\": \"player_uuid\", " +
                                                "\"username\": \"old_username\"}")})),
                @ApiResponse(
                        description = "CONFLICT: Exists other player with the username provided.",
                        responseCode = "409",
                        content = @Content),
                @ApiResponse(
                        description = "BAD REQUEST: request body fails validations",
                        responseCode = "400",
                        content = @Content),
                @ApiResponse(
                        description = "UNAUTHORIZED: a valid access jwt must be provided.",
                        responseCode = "401",
                        content = @Content),
                @ApiResponse(
                        description = "FORBIDDEN: only authorized if request done by registered player.",
                        responseCode = "403",
                        content = @Content)
        })
@Target(METHOD)
@Retention(RUNTIME)
public @interface UpdateUserOperation {
}
