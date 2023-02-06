package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.ValidDices;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Builder
public class RollDto {

    @ValidDices
    private int[] dicesValues;

    private String result;

    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime instant;

    public RollDto() {
        // for no args constructor when deserialization
    }

    public void setDicesValues(int[] dicesValues) {
        this.dicesValues = dicesValues;
    }
}
