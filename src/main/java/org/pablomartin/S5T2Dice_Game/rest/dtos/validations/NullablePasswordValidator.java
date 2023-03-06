package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@PropertySource("classpath:values.properties")
@Log4j2
public class NullablePasswordValidator implements ConstraintValidator<NullableValidPassword,String> {

    @Value("${player.password.length.min}")
    private int min;

    @Value("${player.password.length.max}")
    private int max;

    @Override
    public void initialize(NullableValidPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext) {
        if (password == null){
            return true;
        }else {
            return password.length()>= min && password.length()<= max && !password.isBlank();
        }
    }
}
