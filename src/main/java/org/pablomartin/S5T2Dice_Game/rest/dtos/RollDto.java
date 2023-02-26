package org.pablomartin.S5T2Dice_Game.rest.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import org.pablomartin.S5T2Dice_Game.rest.dtos.validations.ValidDices;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.annotation.JsonProperty.Access.READ_ONLY;

@Getter
@Builder
public class RollDto {

    @ValidDices
    private int[] dicesValues;

    @JsonProperty(access = READ_ONLY)
    private String result;

    @JsonProperty(access = READ_ONLY)
    @JsonFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    private LocalDateTime instant;

    public RollDto() {
        // for no args constructor when deserialization
    }

    //for builder
    private RollDto(int[] dicesValues, String result, LocalDateTime instant) {
        this.dicesValues = dicesValues;
        this.result = result;
        this.instant = instant;
    }

    public void setDicesValues(int[] dicesValues) {
        this.dicesValues = dicesValues;
    }
}
