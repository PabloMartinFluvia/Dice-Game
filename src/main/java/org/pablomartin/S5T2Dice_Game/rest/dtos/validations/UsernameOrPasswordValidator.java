package org.pablomartin.S5T2Dice_Game.rest.dtos.validations;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.pablomartin.S5T2Dice_Game.rest.dtos.CredentialsDto;

public class UsernameOrPasswordValidator implements ConstraintValidator<org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UsernameOrPassword, CredentialsDto> {

    @Override
    public void initialize(org.pablomartin.S5T2Dice_Game.rest.dtos.validations.UsernameOrPassword constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

    @Override
    public boolean isValid(CredentialsDto dto, ConstraintValidatorContext context) {
        return dto.getUsername() != null || dto.getPassword() != null;
    }
}
