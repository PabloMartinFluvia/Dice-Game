package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

import java.util.stream.IntStream;

@PropertySource("classpath:values.properties")
public class DicesValidator implements ConstraintValidator<ValidDices,byte[]> {

    @Value("${dices.numRequired}")
    private int requiredNumDices;

    @Value("${dices.value.max}")
    private byte max;

    @Value("${dices.value.min}")
    private byte min;

    @Override
    public void initialize(ValidDices constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }


    @Override
    public boolean isValid(byte[] dicesValues, ConstraintValidatorContext context) {
        IntStream values = IntStream
                .range(0,dicesValues.length)
                .map(n ->dicesValues[n]);
        return dicesValues.length == requiredNumDices &&
                values.allMatch(v -> v>=min && v<=max );
    }
}
