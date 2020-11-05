package net.thumbtack.school.notes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.notes.validator.NameLength;
import net.thumbtack.school.notes.validator.Password;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordDtoRequest {
    @NotNull(message = "PASSWORD_NOT_SET")
    @NameLength
    @Password
    private String password;
}
