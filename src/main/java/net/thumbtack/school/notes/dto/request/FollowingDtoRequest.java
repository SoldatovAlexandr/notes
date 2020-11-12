package net.thumbtack.school.notes.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.thumbtack.school.notes.validator.Login;
import net.thumbtack.school.notes.validator.NameLength;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowingDtoRequest {
    @NotNull(message = "LOGIN_NOT_SET")
    @NameLength
    @Login
    private String login;
}
