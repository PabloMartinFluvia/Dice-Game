package org.pablomartin.S5T2Dice_Game.exceptions;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Getter
public class ApiErrorResponse {

    private final int statusCode;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private final LocalDateTime timestamp;

    private final String exception;

    private final List<String> causes;

    /*
    Constructor to use when exception message ok to show in the cause of the response
     */
    public ApiErrorResponse (HttpStatus status, Exception ex){
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
        this.exception = ex.getClass().getSimpleName();
        this.causes = List.of(ex.getMessage());
    }

    /*
    Constructor to use for display custom causes
     */
    public ApiErrorResponse (HttpStatus status, Exception ex, String... causes){
        this.statusCode = status.value();
        this.timestamp = LocalDateTime.now();
        this.exception = ex.getClass().getSimpleName();
        this.causes = List.of(causes);
    }
}
