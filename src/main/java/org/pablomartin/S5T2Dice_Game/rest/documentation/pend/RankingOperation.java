package org.pablomartin.S5T2Dice_Game.rest.documentation.pend;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Operation(
        summary = "Request for all players.",
        description = "Generates a summary of the game for each player: average win rate, num of rolls," +
                " username and id.\n\n" +
                "Secured with bearer jwt filter. To be authenticated the JWT must be ACCESS type.",
        responses = {
                @ApiResponse(
                        description = "Provides a list of all summary in 'ranking mode': \n\n" +
                                "A) Players with better win rate go first. \n\n" +
                                "B) Player with more rolls done goes first (num rolls used as criteria when there's a tie with the win rate).",
                        responseCode = "200",
                        content = @Content(examples = @ExampleObject(name = "playersEx", ref = "RankingExample"))),
                @ApiResponse(
                        description = "UNAUTHORIZED: a valid access jwt must be provided.",
                        responseCode = "401",
                        content = @Content)
        }
)
@Target(METHOD)
@Retention(RUNTIME)
public @interface RankingOperation {
}
