package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;

@PropertySource("classpath:values.properties")
public class UsernameValidator implements ConstraintValidator<ValidUsername,String> {

    @Value("${player.username.length.min}")
    private int min;

    @Value("${player.username.length.max}")
    private int max;

    @Override
    public void initialize(ValidUsername constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s!=null && s.length()>= min && s.length()<= max && !s.isBlank();
    }
}
