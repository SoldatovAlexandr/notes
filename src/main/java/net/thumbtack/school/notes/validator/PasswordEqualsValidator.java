package net.thumbtack.school.notes.validator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordEqualsValidator implements ConstraintValidator<PasswordEquals, Object> {
    private String oldPasswordField;
    private String newPasswordField;

    public void initialize(PasswordEquals constraint) {
        oldPasswordField = constraint.oldPassword();
        newPasswordField = constraint.newPassword();
    }

    public boolean isValid(Object o, ConstraintValidatorContext context) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(o);
        String oldPassword = (String) beanWrapper.getPropertyValue(oldPasswordField);
        String newPassword = (String) beanWrapper.getPropertyValue(newPasswordField);
        if (oldPassword == null && newPassword == null) {
            return false;
        }
        return oldPassword == null || !oldPassword.equals(newPassword);
    }
}
