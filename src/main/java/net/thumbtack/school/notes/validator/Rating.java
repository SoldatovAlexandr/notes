package net.thumbtack.school.notes.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = RatingValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Rating {
    String message() default "INVALID_RATING";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
