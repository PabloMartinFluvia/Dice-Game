package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for get all player's rolls.",
        description = "Lists all rolls done by a specific player.\n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be ACCESS type."+
                "Authorized only if id in path is the authenticated user id.",
        parameters = @Parameter(
                name = "id",
                in = ParameterIn.PATH,
                required = true,
                schema = @Schema(type = "string",format = "uuid")),
        responses = {
                @ApiResponse(
                        description = "On success: all player's rolls sorted by instant done. " +
                                "+ Empty if none.",
                        responseCode = "200",
                        content = @Content(examples = @ExampleObject(name = "allrollsEx", ref = "AllRollsExample"))),
                @ApiResponse(
                        description = "UNAUTHORIZED: a valid access jwt must be provided.",
                        responseCode = "401",
                        content = @Content),
                @ApiResponse(
                        description = "FORBIDDEN: only authorized if id specified in path is the authenticated user's id.",
                        responseCode = "403",
                        content = @Content)
        }
)
@Target(METHOD)
@Retention(RUNTIME)
public @interface AllRollsOperation {
}
