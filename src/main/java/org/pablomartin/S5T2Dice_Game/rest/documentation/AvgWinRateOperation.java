package org.pablomartin.S5T2Dice_Game.rest.documentation;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.pablomartin.S5T2Dice_Game.rest.dtos.AverageWinRateDto;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for the average win rate.",
        description = "Calculates the average win rate of the actual players (players without rolls not computed).\n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be ACCESS type.",
        responses = {
                @ApiResponse (
                        description = "On success: provides the average win rate. \n\n" +
                                "+ If there isn't any player or all without rolls: will return 0%.",
                        responseCode = "200",
                        content = @Content(examples = @ExampleObject(value = "{\"average win rate\": \"67.42%\"}"))),
                @ApiResponse(
                        description = "UNAUTHORIZED: not provided a valid bearer access jwt.",
                        responseCode = "401",
                        content = @Content)
        }
)
@Target(METHOD)
@Retention(RUNTIME)
public @interface AvgWinRateOperation {
}
