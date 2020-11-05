package net.thumbtack.school.notes.dto.mappers;

import net.thumbtack.school.notes.dto.request.RegisterUserDtoRequest;
import net.thumbtack.school.notes.dto.request.UpdateUserDtoRequest;
import net.thumbtack.school.notes.dto.response.ProfileInfoDtoResponse;
import net.thumbtack.school.notes.dto.response.UpdateUserDtoResponse;
import net.thumbtack.school.notes.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(unmappedTargetPolicy = ReportingPolicy.IGNORE,
        unmappedSourcePolicy = ReportingPolicy.IGNORE)
public class UserDtoMapper {
    public static final UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);

    public User toUser(RegisterUserDtoRequest registerUserDtoRequest) {
        return new User(
                registerUserDtoRequest.getLogin(),
                registerUserDtoRequest.getPassword(),
                registerUserDtoRequest.getFirstName(),
                registerUserDtoRequest.getLastName(),
                registerUserDtoRequest.getPatronymic());
    }

    public ProfileInfoDtoResponse toProfileInfoDtoResponse(User user) {
        return new ProfileInfoDtoResponse(
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getLogin()
        );
    }

    public User toUser(UpdateUserDtoRequest updateUserDtoRequest) {
        return new User(
                updateUserDtoRequest.getNewPassword(),
                updateUserDtoRequest.getFirstName(),
                updateUserDtoRequest.getLastName(),
                updateUserDtoRequest.getPatronymic());
    }

    public UpdateUserDtoResponse toUpdateUserDtoResponse(User user) {
        return new UpdateUserDtoResponse(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getPatronymic(),
                user.getLogin()
        );
    }
}
