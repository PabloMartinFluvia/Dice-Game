package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UsernameOrPasswordValidator.class)
public @interface UsernameOrPassword {
    String message() default "{credentials.update}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
