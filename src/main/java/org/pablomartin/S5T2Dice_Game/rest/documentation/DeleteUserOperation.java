package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for delete a player.",
        description = "Allows to remove ALL info related to an specific player. \n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be ACCESS type. Authorized only if ADMIN.",
        security = @SecurityRequirement(name = "bearer-jwt"),
        parameters = @Parameter(
                name = "id",
                description = "target player's id",
                in = ParameterIn.PATH,
                required = true,
                schema = @Schema(type = "string",format = "uuid")),
        responses = {
                @ApiResponse(
                        description = "On success: no content. All the target player's info has been removed.",
                        responseCode = "204",
                        content = @Content),
                @ApiResponse(
                        description = "BAD REQUEST: when the target player is actually an admin or " +
                                "the id it's not a valid UUID.",
                        responseCode = "400",
                        content = @Content),
                @ApiResponse(
                        description = "UNAUTHORIZED: a valid access jwt must be provided.",
                        responseCode = "401",
                        content = @Content),
                @ApiResponse(
                        description = "FORBIDDEN: only authorized if request done by admin.",
                        responseCode = "403",
                        content = @Content)
        })
@Target(METHOD)
@Retention(RUNTIME)
public @interface DeleteUserOperation {
}
