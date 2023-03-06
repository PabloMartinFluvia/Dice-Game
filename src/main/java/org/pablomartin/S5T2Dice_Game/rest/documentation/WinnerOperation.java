package org.pablomartin.S5T2Dice_Game.rest.documentation;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for wineer players.",
        description = "Generates a summary of the player with better win rate (many i there's a tie).\n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be ACCESS type.",
        responses = {
                @ApiResponse(
                        description = "Provides a list of players with the maximum win rate",
                        responseCode = "200",
                        content = @Content(examples = @ExampleObject(name = "winnersEx", ref = "WinnersExample"))),
                @ApiResponse(
                        description = "UNAUTHORIZED: a valid access jwt must be provided.",
                        responseCode = "401",
                        content = @Content)
        }
)
@Target(METHOD)
@Retention(RUNTIME)
public @interface WinnerOperation {
}
