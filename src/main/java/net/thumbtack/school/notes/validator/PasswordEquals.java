package net.thumbtack.school.notes.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordEqualsValidator.class)
@Documented
public @interface PasswordEquals {

    String message() default "PASSWORDS_DO_MATCH";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String oldPassword();

    String newPassword();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PasswordEquals[] value();
    }
}
