package net.thumbtack.school.notes.validator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginValidator implements ConstraintValidator<Login, String> {
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        if (s == null) {
            return true;
        }
        Pattern pattern = Pattern.compile("^[a-zA-Zа-яА-Я0-9]+$");
        Matcher matcher = pattern.matcher(s);
        return matcher.find();
    }
}
