package net.thumbtack.school.notes.validator;

import net.thumbtack.school.notes.Config;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class NameLengthValidator implements ConstraintValidator<NameLength, String> {
    private final Config config;

    @Autowired
    public NameLengthValidator(Config config) {
        this.config = config;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || s.length() >= 1 && s.length() <= config.getMaxNameLength();
    }
}
