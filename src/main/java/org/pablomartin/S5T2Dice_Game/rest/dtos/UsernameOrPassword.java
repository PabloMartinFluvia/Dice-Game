package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import org.pablomartin.S5T2Dice_Game.rest.dtos.UsernameOrPasswordValidator;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= UsernameOrPasswordValidator.class)
public @interface UsernameOrPassword {
    String message() default "{credentials.update}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
