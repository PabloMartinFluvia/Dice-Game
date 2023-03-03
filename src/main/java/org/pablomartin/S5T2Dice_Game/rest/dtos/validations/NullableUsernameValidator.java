package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.pablomartin.S5T2Dice_Game.domain.models.DiceGamePathsContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:values.properties")
public class NullableUsernameValidator implements ConstraintValidator<NullableValidUsername,String> {

    @Value("${player.username.length.min}")
    private int min;

    @Value("${player.username.length.max}")
    private int max;

    @Override
    public void initialize(NullableValidUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if(s== null){
            return true;
        }else {
            return s.length() >= min && s.length() <= max && !s.isBlank()
                            && !s.equalsIgnoreCase(DiceGamePathsContext.getDefaultUsername());
        }
    }
}
