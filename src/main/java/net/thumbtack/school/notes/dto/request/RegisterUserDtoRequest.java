package net.thumbtack.school.notes.dto.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.notes.validator.Login;
import net.thumbtack.school.notes.validator.Name;
import net.thumbtack.school.notes.validator.NameLength;
import net.thumbtack.school.notes.validator.Password;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDtoRequest {
    @NotNull(message = "FIRST_NAME_NOT_SET")
    @NameLength
    @Name(message = "INVALID_FIRST_NAME")
    private String firstName;
    @NotNull(message = "LAST_NAME_NOT_SET")
    @NameLength
    @Name(message = "INVALID_LAST_NAME")
    private String lastName;
    @NameLength
    @Name(message = "INVALID_PATRONYMIC")
    private String patronymic;
    @NotNull(message = "LOGIN_NOT_SET")
    @NameLength
    @Login
    private String login;
    @NotNull(message = "PASSWORD_NOT_SET")
    @NameLength
    @Password
    private String password;
}
