package net.thumbtack.school.notes.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class RatingValidator implements ConstraintValidator<Rating, Integer> {
    @Override
    public boolean isValid(Integer i, ConstraintValidatorContext constraintValidatorContext) {
        if (i == null) {
            return true;
        }
        return i <= 5 && i >= 1;
    }
}
