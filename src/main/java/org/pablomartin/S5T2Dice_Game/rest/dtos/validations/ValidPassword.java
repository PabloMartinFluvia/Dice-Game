package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy=PasswordValidator.class)
public @interface ValidPassword {
    String message() default "{singup.password}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
