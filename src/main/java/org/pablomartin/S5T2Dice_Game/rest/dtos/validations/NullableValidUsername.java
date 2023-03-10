package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy= NullableUsernameValidator.class)
public @interface NullableValidUsername {
    String message() default "{singup.username}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
