package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.pablomartin.S5T2Dice_Game.rest.dtos.RollDto;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for post a new roll.",
        description = "Saves a new roll. It's linked to the player wich id is specified in path.\n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be ACCESS type."+
                "Authorized only if id in path is the authenticated user id.",
        parameters = @Parameter(
                name = "id",
                in = ParameterIn.PATH,
                required = true,
                schema = @Schema(type = "string",format = "uuid")),
        requestBody = @RequestBody(
                description = "Dto with the values of dices.",
                required = true,
                content = @Content(
                        schema = @Schema(ref = "PostNewRoll"),
                        examples = @ExampleObject(value = "{\"dicesValues\": [2,5]}")
                )
        ),
        responses = {
                @ApiResponse(
                        description = "On success: the roll's details, specially if it's a winner one or not.",
                        responseCode = "201",
                        content = @Content(examples = @ExampleObject(name = "rollEx", ref = "NewRollExample"))),
                @ApiResponse(
                        description = "BAD REQUEST: request body fails validations",
                        responseCode = "400",
                        content = @Content),
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
public @interface NewRollOperation {
}
