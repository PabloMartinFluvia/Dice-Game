package org.pablomartin.S5T2Dice_Game.utils;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class PercentageSerializer extends JsonSerializer<Float> {

    @Override
    public void serialize(Float value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        float percentage = new BigDecimal(value*100).setScale(2, RoundingMode.HALF_UP).floatValue();
        gen.writeString(String.format("%s %%",percentage));
    }
}
