package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= DicesValidator.class)
public @interface ValidDices {
    String message() default "{game.rolls}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
