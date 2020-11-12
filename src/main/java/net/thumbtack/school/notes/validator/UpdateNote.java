package net.thumbtack.school.notes.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UpdateNoteValidator.class)
@Documented
public @interface UpdateNote {
    String message() default "PARAMS_NOT_SET";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String first();

    String second();

    @Target({ElementType.TYPE})
    @Retention(RetentionPolicy.RUNTIME)
    @Documented
    @interface List {
        PasswordEquals[] value();
    }
}
