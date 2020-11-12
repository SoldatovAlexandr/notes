package net.thumbtack.school.notes.validator;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UpdateNoteValidator implements ConstraintValidator<UpdateNote, Object> {
    private String bodyField;
    private String sectionIdField;

    public void initialize(UpdateNote constraint) {
        bodyField = constraint.first();
        sectionIdField = constraint.second();
    }

    @Override
    public boolean isValid(Object o, ConstraintValidatorContext constraintValidatorContext) {
        BeanWrapper beanWrapper = new BeanWrapperImpl(o);
        String body = (String) beanWrapper.getPropertyValue(bodyField);
        Integer sectionId = (Integer) beanWrapper.getPropertyValue(sectionIdField);
        boolean hasBody = body != null;
        boolean hasSectionId = sectionId != null;
        return hasBody || hasSectionId;
    }
}
