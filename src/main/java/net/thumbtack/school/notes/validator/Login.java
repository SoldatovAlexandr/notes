package net.thumbtack.school.notes.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = LoginValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Login {
    String message() default "INVALID_LOGIN";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
