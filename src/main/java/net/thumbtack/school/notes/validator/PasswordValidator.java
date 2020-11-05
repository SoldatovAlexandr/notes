package net.thumbtack.school.notes.validator;

import net.thumbtack.school.notes.Config;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private final Config config;

    @Autowired
    public PasswordValidator(Config config) {
        this.config = config;
    }

    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        return s == null || s.length() >= config.getMinPasswordLength();
    }
}
